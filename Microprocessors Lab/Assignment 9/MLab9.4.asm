.DSEG
_tmp_: .byte 2

.CSEG


start:
ldi r24,low(RAMEND)
out SPL,r24
ldi r24,high(RAMEND)
out SPH,r24
ser r26
out DDRD, r26
ldi r26,0xF0
out DDRC,r26
clr r26
sts _tmp_,r26
rcall lcd_init
ldi r24, 'N'
rcall lcd_data
ldi r24, 'O'
rcall lcd_data
ldi r24, 'N'
rcall lcd_data
ldi r24, 'E'
rcall lcd_data	

main:
ldi r24, 30
call scan_keypad_rising_edge
call keypad_to_ascii
cpi r24, 0
breq main
push r24
ldi r24, 0x01 ;clear lcd screen
rcall lcd_command
ldi r24, low(1530)
ldi r25, high(1530)
rcall wait_usec
pop r24
rcall lcd_data
jmp main



.include "routines.inc"
