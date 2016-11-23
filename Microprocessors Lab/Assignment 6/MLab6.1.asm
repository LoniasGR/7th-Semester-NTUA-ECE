;
; As6Ex1.asm
;
; Created: 21/11/2016 1:27:36 μμ
; Author : Leonidas Avdelas
;


reset:
	ldi r24,low(RAMEND)
	out SPL,r24
	ldi r24,high(RAMEND)
	out SPH,r24

start:
    ser r26			
	out DDRA,r26	;enable A datapath for write
	clr r26
	out DDRB,r26	;enable B datapath for read
	ldi r26,0x80	;we start from 1000 0000
	ldi r27,0		;shifting right

main:
	ldi r24,low(500)	;
	ldi r25,high(500)	;0.5 sec == 500 msec
	out PORTA,r26		;light the led then wait
	rcall wait_msec
	
stop_check:
	in r28,PINB		;check if dip switch is pressed
	andi r28,1
	cpi r28,1
	breq stop_check
	jmp check_edges	;check if LED reached an edge

direction:
	cpi r27,0
	breq left
	brne right

right:
	lsl r26
	jmp main

left:
	lsr r26
	jmp main

check_edges:
	cpi r26,1
	breq right_edge	;the lit LED is at the right edge
	cpi r26,0x80
	breq left_edge	;the lit LED is at the left edge
	jmp direction

right_edge:		;check if LED is going right (we are at the right edge)
	cpi r27,0	;then swap to going left
	breq left_to_right
	jmp direction


left_to_right:
	ldi r27,1
	jmp direction


left_edge:	;same as above
	cpi r27,1
	breq right_to_left
	jmp direction


right_to_left:
	ldi r27,0
	jmp direction

wait_usec: ;1 usec delay
	sbiw r24,1
	nop
	nop
	nop
	nop
	brne wait_usec
	ret

wait_msec: ;delay depending on r25r24. 1msec each
	push r24
	push r25
	ldi r24,low(998)
	ldi r25,high(998)
	rcall wait_usec
	pop r25
	pop r24
	sbiw r24,1
	brne wait_msec
	ret