write_2_nibbles:
;input: r24 - byte
;output: -
;registers used: r24:r25

push r24		; στέλνει τα 4 MSB
in r25 ,PIND	; διαβάζονται τα 4 LSB και τα ξαναστέλνουμε
andi r25 ,0x0f	; για να μην χαλάσουμε την όποια προηγούμενη κατάσταση
andi r24 ,0xf0	; απομονώνονται τα 4 MSB και
add r24 ,r25	; συνδυάζονται με τα προϋπάρχοντα 4 LSB
out PORTD ,r24	; και δίνονται στην έξοδο
sbi PORTD ,PD3	; δημιουργείται παλμός Enable στον ακροδέκτη PD3
cbi PORTD ,PD3	; PD3=1 και μετά PD3=0
pop r24			; στέλνει τα 4 LSB. Ανακτάται το byte.
swap r24		; εναλλάσσονται τα 4 MSB με τα 4 LSB
andi r24 ,0xf0	; που με την σειρά τους αποστέλλονται
add r24 ,r25
out PORTD ,r24
sbi PORTD ,PD3	; Νέος παλμός Enable
cbi PORTD ,PD3
ret

lcd_data:
;input: r24 - byte
;output: -
;registers used: r24:r25

sbi PORTD ,PD2			; επιλογή του καταχωρήτη δεδομένων (PD2=1)
rcall write_2_nibbles	; αποστολή του byte
ldi r24 ,43				; αναμονή 43μsec μέχρι να ολοκληρωθεί η λήψη
ldi r25 ,0				; των δεδομένων από τον ελεγκτή της lcd
rcall wait_usec
ret

lcd_command:
;input: r24 - byte
;output: -
;registers used: r24:r25

cbi PORTD ,PD2			; επιλογή του καταχωρητή εντολών (PD2=1)
rcall write_2_nibbles	; αποστολή της εντολής και αναμονή 39μsec
ldi r24 ,39				; για την ολοκλήρωση της εκτέλεσης της από τον ελεγκτή της lcd.
ldi r25 ,0				; ΣΗΜ.: υπάρχουν δύο εντολές, οι clear display και return home,
rcall wait_usec			; που απαιτούν σημαντικά μεγαλύτερο χρονικό διάστημα.
ret

lcd_init:
;input: -
;output: -
;registers used: r24:r25

ldi r24 ,40			; Όταν ο ελεγκτής της lcd τροφοδοτείται με
ldi r25 ,0			; ρεύμα εκτελεί την δική του αρχικοποίηση.
rcall wait_msec		; Αναμονή 40 msec μέχρι αυτή να ολοκληρωθεί.
ldi r24 ,0x30		; εντολή μετάβασης σε 8 bit mode
out PORTD ,r24		; επειδή δεν μπορούμε να είμαστε βέβαιοι
sbi PORTD ,PD3		; για τη διαμόρφωση εισόδου του ελεγκτή
cbi PORTD ,PD3		; της οθόνης, η εντολή αποστέλλεται δύο φορές
ldi r24 ,39
ldi r25 ,0			; εάν ο ελεγκτής της οθόνης βρίσκεται σε 8-bit mode
rcall wait_usec		; δεν θα συμβεί τίποτα, αλλά αν ο ελεγκτής έχει διαμόρφωση
					; εισόδου 4 bit θα μεταβεί σε διαμόρφωση 8 bit
ldi r24 ,0x30
out PORTD ,r24
sbi PORTD ,PD3
cbi PORTD ,PD3
ldi r24 ,39
ldi r25 ,0
rcall wait_usec
ldi r24 ,0x20		; αλλαγή σε 4-bit mode
out PORTD ,r24
sbi PORTD ,PD3
cbi PORTD ,PD3
ldi r24 ,39
ldi r25 ,0
rcall wait_usec
ldi r24 ,0x28		; επιλογή χαρακτήρων μεγέθους 5x8 κουκίδων
rcall lcd_command	; και εμφάνιση δύο γραμμών στην οθόνη
ldi r24 ,0x0c		; ενεργοποίηση της οθόνης, απόκρυψη του κέρσορα
rcall lcd_command
ldi r24 ,0x01		; καθαρισμός της οθόνης
rcall lcd_command
ldi r24 ,low(1530)
ldi r25 ,high(1530)
rcall wait_usec
ldi r24 ,0x06		; ενεργοποίηση αυτόματης αύξησης κατά 1 της διεύθυνσης
rcall lcd_command	; που είναι αποθηκευμένη στον μετρητή διευθύνσεων και
					; απενεργοποίηση της ολίσθησης ολόκληρης της οθόνης
ret

wait_usec: ;each cycle is 1 usec delay
;input: r24 - byte
;output: -
;registers used: r24

	sbiw r24,1
	nop
	nop
	nop
	nop
	brne wait_usec
	ret

wait_msec: ;delay depending on r25r24. 1msec each cycle
;input: r25:r24
;output: -
;registers used: r24:r25

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

scan_row:
;input: line number - r24
;output: in each of the 4 LSB of r24 each key's condition  saved 
;registers used: r24:r25

ldi r25 ,0x08	; αρχικοποίηση με 0000 1000
back_: 
lsl r25	; αριστερή ολίσθηση του 1 τόσες θέσεις
dec r24			; όσος είναι ο αριθμός της γραμμής
brne back_
out PORTC ,r25	; η αντίστοιχη γραμμή τίθεται στο λογικό 1
nop
nop				; καθυστέρηση για να προλάβει να γίνει η αλλαγή κατάστασης
in r24 ,PINC	; επιστρέφουν οι θέσεις (στήλες) των διακοπτών που είναι πιεσμένοι
andi r24 ,0x0f	; απομονώνονται τα 4 LSB όπου τα 1 δείχνουν που είναι πατημένοι
ret				; οι διακόπτες.

scan_keypad:
;input: -
;output: r25:r24 - the condition of each key of the keypad
;registers used: r27:r26, r25:r24

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



scan_keypad_rising_edge:
;input: r24 - expected bounce time for the keypad	
;output: r25:r24 - the condition of each key of the keypad (keys that were now pressed)
;registers used: r27:r26, r24:r25, r23:r22

mov r22 ,r24			; αποθήκευσε το χρόνο σπινθηρισμού στον r22
rcall scan_keypad		; έλεγξε το πληκτρολόγιο για πιεσμένους διακόπτες
push r24				; και αποθήκευσε το αποτέλεσμα
push r25
mov r24 ,r22			; καθυστέρησε r22 ms (τυπικές τιμές 10-20 msec που καθορίζεται από τον
ldi r25 ,0				; κατασκευαστή του πληκτρολογίου  χρονοδιάρκεια σπινθηρισμών)
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

keypad_to_ascii: ; λογικό 1 στις θέσεις του καταχωρητή r26 δηλώνουν
;input: r25:r24 - result of scan_keypad, scan_keypad_rising_edge (16 bits - one for each key (pressed or not))	
;output: r24 - 0 in nothing is pressed, else ASCII code of the specific character
;registers used: r27:r26, r24:r25

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
sbrc r26 ,3		; αν δεν είναι 1παρακάμπτει την ret, αλλιώς (αν είναι 1)
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
ldi r24 ,'4'	; λογικό 1 στις θέσεις του καταχωρητή r27 δηλώνουν
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