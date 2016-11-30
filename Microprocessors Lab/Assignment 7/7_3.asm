.include "m16def.inc"
.def temp=r16
.def mule=r17
.def clock_h=r18
.def clock_l=r19
.def leds=r20
.org 0x00
	jmp main	; Reset Handler
.org 0x02	
	jmp ISR0	; IRQ0  Handler
.org 0x10
	jmp timer1_rout
main:
	ldi temp,high(RAMEND)
	out SPH,temp
	ldi temp,low(RAMEND)
	out SPL,temp			; stack init
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	ldi r24 ,( 1 << ISC01) | ( 1 << ISC00)
	out MCUCR, r24
	ldi r24 ,( 1 << INT0)
	out GICR, r24

	ldi temp,0x05			; Frequency Divisor = 1024
	out TCCR1B,temp			
	ldi temp,0x04			; Enable Timer1
	out TIMSK,temp	
	sei
	
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
;	clr temp				; Setting PORTD as input
;	out DDRD,temp			; pullup resistance for input
	clr temp
	out DDRD,temp			; pullup resistance for input
	ser temp
;	out PORTD,temp
;	ldi temp,0x02			
	ser temp
	out DDRA,temp
;	ldi temp,0xfd			
;	out PORTA,temp
	
	ldi clock_h,0xa4		; clock_h:clock_l = 0xA472 = 0d42098 {=65536-3*7812.5}, where 7812.5Hz = cycles/sec =~ 8MHz/1024
	ldi clock_l,0x72		;
	ldi leds,0x80			; PA7
loop:
	in mule,PINB			; portb -> input
	sbrs mule,0			    ; If PB0==1 exit loop and do stuff with leds
	rjmp loop			
	ldi temp,0xFF
	out PORTA,temp
	ldi r24,low(500) 	
	ldi r25,high(500) 	    ; delay 500 ms
	rcall wait_msec
	out PORTA,leds			; led on due to PB0
	out TCNT1H,clock_h		; 3secs
	out TCNT1L,clock_l
	rjmp loop				
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
ISR0:
	rcall protect			
	push r26
	in r26, SREG
	push r26
	ldi temp,0xFF			; on refresh, light em up
	out PORTA,temp
	ldi r24,low(500) 	
	ldi r25,high(500) 	    ; delay 500 ms
	rcall wait_msec
	out PORTA,leds			; led on
	out TCNT1H,clock_h		; reset clock
	out TCNT1L,clock_l
	pop r26
	out SREG, r26
	pop r26
	sei						
	reti				
	
timer1_rout:
	clr leds				
	out PORTA,leds			; timer overflow -> lights out
	ldi leds,0x80			; led PA7 needs to light up again
	sei						
	reti

wait_usec:
	sbiw r24 ,1		; 2 cycles (0.250 micro sec)
	nop				; 1 cycles (0.125 micro sec)
	nop				; 1 cycles (0.125 micro sec)
	nop				; 1 cycles (0.125 micro sec)
	nop				; 1 cycles (0.125 micro sec)
	brne wait_usec	; 1 or 2 cycles (0.125 or 0.250 micro sec)
	ret				; 4 cycles (0.500 micro sec)

wait_msec:
	push r24		; 2 cycles (0.250 micro sec)
	push r25		; 2 cycles
	ldi r24 , 0xe6	; load register r25:r24 with 998 (1 cycles - 0.125 micro sec)
	ldi r25 , 0x03	; 1 cycles (0.125 micro sec)
	rcall wait_usec	; 3 cycles (0.375 micro sec), total delay 998.375 micro sec
	pop r25			; 2 cycles (0.250 micro sec)
	pop r24			; 2 cycles
	sbiw r24 , 1	; 2 cycles
	brne wait_msec	; 1 or 2 cycles (0.125 or 0.250 micro sec)
	ret				; 4 cycles (0.500 micro sec)	

protect:
	ldi temp,0x40		;0b0100 0000
	out GIFR,temp		;Setting zero INTF0
	ldi r24,0x05		
	ldi r25,0x00
	rcall wait_msec		;wait 5 msec
	in temp,GIFR		;Check if INTF0==1
	sbrc temp,6			
	rjmp protect		;If INTF0==1 loop
	ret					;If INTF0==0 return
