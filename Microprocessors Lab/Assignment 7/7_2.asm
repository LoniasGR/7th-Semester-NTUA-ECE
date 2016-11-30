.include "m16def.inc"
.def reg = r24
.def temp = r26

.org 0x0
rjmp reset
.org 0x4
rjmp routine2
reti

reset:
		ldi temp,high(RAMEND)					;Initialize stack
		out SPH, temp
		ldi temp,low(RAMEND)
		out SPL, temp
		ldi reg, (1 << ISC11) | (1 << ISC10)	;Set the options for interrupt INT0
		out MCUCR, reg
		ldi reg, (1 << INT1)
		out GICR, reg
		sei										;Enable interrupts
		clr reg
		out DDRB,reg
		ser temp
		out DDRC, temp							;PORTB is output (interrupts' counter)
		out DDRA, temp							;PORTA is output (counter from 1 to 255)
		ldi temp, 0								;PORTD is the input .Enable only PD0 and PD3 (Interrupt)
		out DDRD, temp
		ldi temp, 0xFF
loop:	
		inc temp								;Increase the counter
		out PORTA, temp							;Display to PORTA
		ldi r24, low(100)						;Wait for 0.10 sec (100 msec)
		ldi r25, high(100)
		rcall wait_msec
		rjmp loop								;Continuous operation (when the counter reaches 255 it resets)

;----------------------------------------------------------------------------------------------------------------

routine2:
		ldi r24, low(5)
		ldi r25, high(5)
jumpee:
		ldi reg, (1 << INTF1)		;Check for misses when the PD3 button is pressed
		out GIFR, reg				;Make sure that only one interrupt will be processed
		rcall wait_msec
		in reg, GIFR
		sbrc reg,7
		rjmp jumpee					;End loop
		push temp		
		in temp, SREG
		push temp
		ldi r20,0
		ldi r19,8
		in r18,PINB
again:
		lsr r18
		brcc next
		inc r20
next:
		dec r19
		cpi r19,0
		brne again
	    
		out PORTC,r20
		pop temp
		out SREG,temp
		pop temp
		reti

;----------------------------------------------------------------------------------------------------------------
wait_usec:
	sbiw r24 ,1 		
	nop 				
	nop 				
	nop 				
	nop 				
	brne wait_usec  	
	ret 


wait_msec:
	push r24 			
	push r25 			
	ldi r24 , low(998)  
	ldi r25 , high(998) 
	rcall wait_usec
	pop r25 		
	pop r24 		
	sbiw r24 , 1	
	brne wait_msec  
	ret
