;
; AssemblerApplication2.asm
;
; Created: 6/12/2016 2:46:38
; Author : Leonidas Avdelas
;
.include "m16def.inc"

; ----
.DSEG
_tmp_: .byte 2

.CSEG

start:
ldi r24,low(RAMEND)
out SPL,r24
ldi r24,high(RAMEND)
out SPH,r24
ser r26
out DDRA, r26
ldi r26,0xF0
out DDRC,r26				;Set PORTC suitably for keyboard input
clr r26
sts _tmp_,r26



main:
ldi r24, 30
call scan_keypad_rising_edge
call keypad_to_ascii
cpi r24, 0
breq main
cpi r24, '0'
brne main

one_pressed:
ldi r24, 30
call scan_keypad_rising_edge
call keypad_to_ascii
cpi r24,0
breq one_pressed
cpi r24,'4'
brne main

two_pressed:
ldi r27, 0x0A
ldi r24, low(250)
ldi r25, high(250)

loop:
ser r26
out PORTA, r26
ldi r24, low(250)
ldi r25, high(250)
call wait_msec
clr r26
out PORTA, r26
ldi r24, low(250)
ldi r25, high(250)
call wait_msec
dec r27
breq main
jmp loop

scan_row:
 ldi r25 ,0x08	; ������������ �� �0000 1000�
back_: lsl r25	; �������� �������� ��� �1� ����� ������
dec r24			; ���� ����� � ������� ��� �������
 brne back_
out PORTC ,r25	; � ���������� ������ ������� ��� ������ �1�
nop
nop				; ����������� ��� �� �������� �� ����� � ������ ����������
in r24 ,PINC	; ����������� �� ������ (������) ��� ��������� ��� ����� ���������
andi r24 ,0x0f	; ������������� �� 4 LSB ���� �� �1� �������� ��� ����� ���������
ret				; �� ���������.

scan_keypad:
 ldi r24 ,0x01	; ������ ��� ����� ������ ��� �������������
 rcall scan_row
swap r24		; ���������� �� ����������
mov r27 ,r24	; ��� 4 msb ��� r27
ldi r24 ,0x02	; ������ �� ������� ������ ��� �������������
rcall scan_row
add r27 ,r24	; ���������� �� ���������� ��� 4 lsb ��� r27
 ldi r24 ,0x03	; ������ ��� ����� ������ ��� �������������
 rcall scan_row
swap r24		; ���������� �� ����������
mov r26 ,r24	; ��� 4 msb ��� r26
ldi r24 ,0x04	; ������ ��� ������� ������ ��� �������������
rcall scan_row
add r26 ,r24	; ���������� �� ���������� ��� 4 lsb ��� r26
movw r24 ,r26	; �������� �� ���������� ����� ����������� r25:r24
ret



scan_keypad_rising_edge:
 mov r22 ,r24			; ���������� �� ����� ������������ ���� r22
rcall scan_keypad		; ������ �� ������������ ��� ���������� ���������
push r24				; ��� ���������� �� ����������
push r25
mov r24 ,r22			; ����������� r22 ms (������� ����� 10-20 msec ��� ����������� ��� ���
ldi r25 ,0				; ������������ ��� ������������� � ������������� ������������)
rcall wait_msec
rcall scan_keypad		; ������ �� ������������ ���� ��� ��������
pop r23					; ��� ������� ���������� �����������
pop r22
and r24 ,r22
and r25 ,r23
ldi r26 ,low(_tmp_)		; ������� ��� ��������� ��� ��������� ����
ldi r27 ,high(_tmp_)	; ����������� ����� ��� �������� ����� r27:r26
 ld r23 ,X+
ld r22 ,X
st X ,r24				; ���������� ��� RAM �� ��� ���������
st -X ,r25				; ��� ���������
com r23
com r22					; ���� ���� ��������� ��� ����� ������� �������
and r24 ,r22
and r25 ,r23
ret

keypad_to_ascii: ; ������ �1� ���� ������ ��� ���������� r26 ��������
 movw r26 ,r24	 ; �� �������� ������� ��� ��������
ldi r24 ,'*'
sbrc r26 ,0
ret
ldi r24 ,'0'
sbrc r26 ,1
ret
ldi r24 ,'#'
sbrc r26 ,2
ret
ldi r24 ,'D'
sbrc r26 ,3		; �� ��� ����� �1������������ ��� ret, ������ (�� ����� �1�)
ret				; ���������� �� ��� ���������� r24 ��� ASCII ���� ��� D.
ldi r24 ,'7'
sbrc r26 ,4
ret
ldi r24 ,'8'
sbrc r26 ,5
ret
ldi r24 ,'9'
sbrc r26 ,6
ret
ldi r24 ,'C'
sbrc r26 ,7
 ret
 ldi r24 ,'4'	; ������ �1� ���� ������ ��� ���������� r27 ��������
sbrc r27 ,0		; �� �������� ������� ��� ��������
ret
ldi r24 ,'5'
sbrc r27 ,1
ret
ldi r24 ,'6'
sbrc r27 ,2
ret
ldi r24 ,'B'
sbrc r27 ,3
ret
ldi r24 ,'1'
sbrc r27 ,4
ret
ldi r24 ,'2'
sbrc r27 ,5
ret
ldi r24 ,'3'
sbrc r27 ,6
ret
ldi r24 ,'A'
sbrc r27 ,7
ret
clr r24
ret

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
