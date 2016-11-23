;
; AssemblerApplication1.asm
;
; Created: 11/22/2016 6:17:43 PM
; Author : Gbax
;

.include "m16def.inc"
.def temp = r16
.cseg
.org 0
ldi temp,HIGH(RAMEND)
out SPH,temp
ldi temp,LOW(RAMEND)
out SPL,temp	
ser r26 	    ; PORTA: OUTPUT
out DDRA, r26 	
clr r26
out DDRB, r26   ; PORTB: INPUT
ser r26         
;out PORTB, r26

; r23: INPUT
; r22: INPUT COPY
main:
	in r23, PINB    
    mov r22, r23
	andi r23, 0x0f  ; 4 LSBs 
	cpi r23, 0x00   ; check if pb00-pb03 == 0
	brne n1
	rcall off
	rjmp main
n1:
	in r23, PINB    
    mov r22, r23
	swap r22        ; 4 MSBs
	andi r22, 0x0f
	cpi r22, 0x00 	; check if pb04-pb07 == 0
	brne n2
	andi r23, 0x0f
	brne n11
	rcall off
	rjmp main
n11:
	rcall on
	rjmp n1
n2:
	rcall on 	
wait_on:
	andi r23, 0x0f  ; 4 LSBs 
	ldi r24, 0x64   ; 0x64 = 100 ms delay
	ldi r25, 0x00
	rcall wait_msec ; r23*100 ms
	dec r23
	brne wait_on

	rcall off 	    ; leds off
;	swap r22        ; 4 MSBs 
;	andi r22, 0x0f

wait_off:
;	andi r23, 0x0f  ; 4 LSBs 
	ldi r24, 0x64   ; 100 ms
	ldi r25, 0x00   ; 0x64 = 100
	rcall wait_msec ; r22*100 ms
	dec r22
	brne wait_off
	rjmp main 	
on:
	ser r26 
	out PORTA , r26
	ret 	

off:
	clr r26 	
	out PORTA , r26
	ret 		

wait_usec:
	sbiw r24 ,1 	; 2 ?????? (0.250 µsec)
	nop 		; 1 ?????? (0.125 µsec)
	nop 		; 1 ?????? (0.125 µsec)
	nop 		; 1 ?????? (0.125 µsec)
	nop 		; 1 ?????? (0.125 µsec)
	brne wait_usec 	; 1 ? 2 ?????? (0.125 ? 0.250 µsec)
	ret 		; 4 ?????? (0.500 µsec)

wait_msec:
	push r24 	; 2 ?????? (0.250 µsec)
	push r25 	; 2 ??????
	ldi r24 , 0xe6	; f??t?se t?? ?ata?. r25:r24 µe 998 (1 ?????? - 0.125 µsec)
	ldi r25 , 0x03	; 1 ?????? (0.125 µsec)
	rcall wait_usec ; 3 ?????? (0.375 µsec), p???a?e? s??????? ?a??st???s? 998.375 µsec
	pop r25 	; 2 ?????? (0.250 µsec)
	pop r24	 	; 2 ??????
	sbiw r24 , 1 	; 2 ??????
	brne wait_msec 	; 1 ? 2 ?????? (0.125 ? 0.250 µsec)
	ret 		; 4 ?????? (0.500 µsec)
