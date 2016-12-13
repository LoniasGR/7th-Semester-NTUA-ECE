.include "m16def.inc"
start:
clr r26
out DDRB, r26
out DDRA, r26
ser r26
out DDRC, r26
ldi r26,0x00
out PORTC, r26

main:
clr r27
clr r28 ;r28 holds the LEDs to be lit
in r26,PINB	
in r29,PINA
;mov r24, 0x10	   ;	
;mov r25, 0x00	   ;
;rcall wait_msec   ;------THIS PART IS ONLY NEEDED IF WE WANT TO CHECK FOR DEBOUNCING
;in r22, PINB      ;
;in r21, PINA      ;
;com r22           ; 
;com r21           ;
;and r26, r22      ;
;and r29, r21      ;
mov r25,r26

PC0check:
andi r26, 0b00000011 ;check if 2 LSBs are 00 
cpi r26, 0	   ;then PC0 is off (OR)
brne PC0on
 
PC1check:
mov r26, r25	;check if bytes 2 & 3 are 11
andi r26, 0b000001100  ;else PC1 is off (AND)
cpi r26, 0b00001100
breq PC1on

PC2check:
mov r26,r25		;check if bytes 4 & 5 are >= 3 or <1
andi r26, 0x30	;if they are PC2 is off (XOR)
swap r26
cpi r26, 0x03
brsh PC2off
cpi r26, 0x01
brlo PC2off
jmp PC2on

PC3check:
mov r26, r25	;first check if bytes 6 & 7 are >=3 or <1
andi r26, 0xC0	;if they are gate 4 is off (XOR)
swap r26
lsr r26
lsr r26
cpi r26, 0x03
brsh gate4off
cpi r26, 0x01
brlo gate4off
jmp gate4on

end:			;if any keys of A are pressed
cpi r29, 0x01	;we reverse the LEDs
brsh reverse
jmp finish

reverse:
com r27
andi r27, 0x0F

finish:
out PORTC, r27
jmp main

PC0on:
ldi r28, 0x01
or r27, r28
jmp PC1check

PC1on:
ldi r28, 0x02
or r27,r28
jmp PC2check

PC2on:
ldi r24,0x01
ldi r28, 0x04
or r27, r28
jmp PC3check

PC2off:
ldi r24, 0x00
jmp PC3check

gate4off:
ldi r26, 0x00
jmp PC3result

gate4on:
ldi r26, 0x01
jmp PC3result

PC3result: ;r24 is the result of gate 3
eor r26,r24 ;r26 is the result of gate 4
lsl r26		;their XOR is the result for PC3 
lsl r26		;so we are rotating to create the correct bit
lsl r26
add r28, r26
jmp end

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
