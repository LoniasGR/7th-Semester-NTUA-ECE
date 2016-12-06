;
; AssemblerApplication2.asm
;
; Created: 6/12/2016 2:46:38 μμ
; Author : Leonidas Avdelas
;

start:
ldi r24,low(RAMEND)
out SPL,r24
ldi r24,high(RAMEND)
out SPH,r24
call scan_keypad_rising_edge
ser r26
out DDRA, r26

main:
call scan_keypad_rising_edge
call keypad_to_ascii
cpi r24, 0
breq main
cpi r24, 'B'
brne main

one_pressed:
call scan_keypad_rising_edge
call keypad_to_ascii
cpi r24,0
breq one_pressed
cpi r24,4
brne main

two_pressed:
ldi r27, 0x0A
ldi r24, 0x19
ldi r25, 0x00

loop:
ser r26
out PORTA, r26
ldi r24, 0x19
ldi r25, 0x00
call wait_msec
clr r26
out PORTA, r26
ldi r24, 0x19
ldi r25, 0x00
dec r27
breq main
jmp loop

scan_row:
 ldi r25 ,0x08	; αρχικοποίηση με ‘0000 1000’
back_: lsl r25	; αριστερή ολίσθηση του ‘1’ τόσες θέσεις
dec r24			; όσος είναι ο αριθμός της γραμμής
 brne back_
out PORTC ,r25	; η αντίστοιχη γραμμή τίθεται στο λογικό ‘1’
nop
nop				; καθυστέρηση για να προλάβει να γίνει η αλλαγή κατάστασης
in r24 ,PINC	; επιστρέφουν οι θέσεις (στήλες) των διακοπτών που είναι πιεσμένοι
andi r24 ,0x0f	; απομονώνονται τα 4 LSB όπου τα ‘1’ δείχνουν που είναι πατημένοι
ret				; οι διακόπτες.

scan_keypad:
 ldi r24 ,0x01	; έλεγξε την πρώτη γραμμή του πληκτρολογίου
 rcall scan_row
swap r24		; αποθήκευσε το αποτέλεσμα
mov r27 ,r24	; στα 4 msb του r27
ldi r24 ,0x02	; έλεγξε τη δεύτερη γραμμή του πληκτρολογίου
rcall scan_row
add r27 ,r24	; αποθήκευσε το αποτέλεσμα στα 4 lsb του r27
 ldi r24 ,0x03	; έλεγξε την τρίτη γραμμή του πληκτρολογίου
 rcall scan_row
swap r24		; αποθήκευσε το αποτέλεσμα
mov r26 ,r24	; στα 4 msb του r26
ldi r24 ,0x04	; έλεγξε την τέταρτη γραμμή του πληκτρολογίου
rcall scan_row
add r26 ,r24	; αποθήκευσε το αποτέλεσμα στα 4 lsb του r26
movw r24 ,r26	; μετέφερε το αποτέλεσμα στους καταχωρητές r25:r24
ret

; ---- Αρχή τμήματος δεδομένων
.DSEG
_tmp_: .byte 2

; ---- Τέλος τμήματος δεδομένων
.CSEG
scan_keypad_rising_edge:
 mov r22 ,r24			; αποθήκευσε το χρόνο σπινθηρισμού στον r22
rcall scan_keypad		; έλεγξε το πληκτρολόγιο για πιεσμένους διακόπτες
push r24				; και αποθήκευσε το αποτέλεσμα
push r25
mov r24 ,r22			; καθυστέρησε r22 ms (τυπικές τιμές 10-20 msec που καθορίζεται από τον
ldi r25 ,0				; κατασκευαστή του πληκτρολογίου – χρονοδιάρκεια σπινθηρισμών)
rcall wait_msec
rcall scan_keypad		; έλεγξε το πληκτρολόγιο ξανά και απόρριψε
pop r23					; όσα πλήκτρα εμφανίζουν σπινθηρισμό
pop r22
and r24 ,r22
and r25 ,r23
ldi r26 ,low(_tmp_)		; φόρτωσε την κατάσταση των διακοπτών στην
ldi r27 ,high(_tmp_)	; προηγούμενη κλήση της ρουτίνας στους r27:r26
 ld r23 ,X+
ld r22 ,X
st X ,r24				; αποθήκευσε στη RAM τη νέα κατάσταση
st -X ,r25				; των διακοπτών
com r23
com r22					; βρες τους διακόπτες που έχουν «μόλις» πατηθεί
and r24 ,r22
and r25 ,r23
ret

keypad_to_ascii: ; λογικό ‘1’ στις θέσεις του καταχωρητή r26 δηλώνουν
 movw r26 ,r24	 ; τα παρακάτω σύμβολα και αριθμούς
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
sbrc r26 ,3		; αν δεν είναι ‘1’παρακάμπτει την ret, αλλιώς (αν είναι ‘1’)
ret				; επιστρέφει με τον καταχωρητή r24 την ASCII τιμή του D.
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
 ldi r24 ,'4'	; λογικό ‘1’ στις θέσεις του καταχωρητή r27 δηλώνουν
sbrc r27 ,0		; τα παρακάτω σύμβολα και αριθμούς
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