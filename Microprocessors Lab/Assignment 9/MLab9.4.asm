
start:.DSEG
_tmp_: .byte 2

.CSEG


ldi r24,low(RAMEND)
out SPL,r24
ldi r24,high(RAMEND)
out SPH,r24
ser r26
out DDRD, r26
clr r26
out DDRA, r26
sts _tmp_,r26

clear:
clr r20 ;seconds
clr r21 ;minutes
clr r19 ;decades minutes
clr r18 ;decades seconds
rcall print

main:
in r24, PINA
mov r25, r24
andi r24, 0x80
cpi r24, 0x80
breq clear
mov r24, r25
andi r24, 0x01
cpi r24, 0x00
breq main

counter:
push r20
push r21
ldi r24, 10
clr r19
clr r18

find_minutes:
cpi r21, 10
brlo minutes_found
sub r21, r24
inc r19
jmp find_minutes

minutes_found:
cpi r20, 10
brlo wait
sub r20, r24
inc r18
jmp minutes_found

wait:
rcall print
ldi r24,low(1000)
ldi r25, high(1000)
rcall wait_msec
pop r21
pop r20
inc r20
cpi r20, 60
breq add_minute
jmp main

add_minute:
clr r20
inc r21
cpi r21, 60
breq reset
jmp main

reset:
clr r21
jmp main


print:
;input: r19 - tenths of mins, r21 - ones of minutes
;		r18 - tenths of seconds, r20 - ones of seconds
; output: -
;uses: r19:r18 r21:r20, r25:r24
ldi r24, 0x01 ;clear lcd screen
rcall lcd_command
ldi r24, low(1530)
ldi r25, high(1530)
rcall wait_usec
mov r24, r19
rcall bin_to_ascii
rcall lcd_data
mov r24, r21
rcall bin_to_ascii
rcall lcd_data
ldi r24, ' '
rcall lcd_data
ldi r24, 'M'
rcall lcd_data
ldi r24, 'I'
rcall lcd_data
ldi r24, 'N'
rcall lcd_data
ldi r24, 0b00111010
rcall lcd_data
mov r24, r18
rcall bin_to_ascii
rcall lcd_data
mov r24, r20
rcall bin_to_ascii
rcall lcd_data
ldi r24, ' '
rcall lcd_data
ldi r24, 'S'
rcall lcd_data
ldi r24, 'E'
rcall lcd_data
ldi r24, 'C'
rcall lcd_data
ret

bin_to_ascii:
;input: r24 - binary of the digit
;output: r24, ascii of the digit
;uses: r24
push r25
mov r25, r24
ldi r24, '0'
cpi r25, 0
breq end
ldi r24, '1'
cpi r25, 1
breq end
ldi r24, '2'
cpi r25, 2
breq end
ldi r24, '3'
cpi r25, 3
breq end
ldi r24, '4'
cpi r25, 4
breq end
ldi r24, '5'
cpi r25, 5
breq end
ldi r24, '6'
cpi r25, 6
breq end
ldi r24, '7'
cpi r25, 7
breq end
ldi r24, '8'
cpi r25, 8
breq end
ldi r24, '9'
cpi r25, 9
breq end

end:
pop r25
ret



.include "routines.inc"

