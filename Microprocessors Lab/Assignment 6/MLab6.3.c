/*
 * GccApplication1.c
 *
 * Created: 21/11/2016 3:29:01 μμ
 * Author : Leonidas Avdelas
 */ 

#include <avr/io.h>
#include <stdbool.h>
int rotate_LED_once (int direction, int LED)
{
	if (direction == -1) {
		if(LED == 0x80) 
			return 0x01;
		else 
			return LED << 1;
	}
	else {
		if (LED == 0x01)
			return 0x80;
		else
			return LED >> 1;
	}
}

int rotate_LED (int direction, int times, int LED)
{
	for (int i=0; i < times; i++) {
		LED = rotate_LED_once(direction, LED);
	}
	return LED;
}
int main(void)
{
	DDRB = 0xFF; /* port B is output */
	DDRD = 0x00; /* port D is input */
	int LED = 0x01;
	bool pressed_buttons[5];
	
	for (int i = 0; i < 5; i++)
		pressed_buttons[i] = false;
	
	while(1) {
		PORTB = LED;
		if (bit_is_set(PIND, PIND0)) 
			pressed_buttons[0] = true;
		if (bit_is_set(PIND, PIND1))
			pressed_buttons[1] = true;
		if (bit_is_set(PIND,PIND2))
			pressed_buttons[2] = true;
		if (bit_is_set(PIND,PIND3))
			pressed_buttons[3] = true;
		if (bit_is_set(PIND,PIND4))
			pressed_buttons[4] = true;
		
		if (bit_is_clear(PIND,PIND0) && (pressed_buttons[0] == true)) {
			LED = rotate_LED(-1, 1, LED);
			pressed_buttons[0] = false;
		}
		if (bit_is_clear(PIND,PIND1) && (pressed_buttons[1] == true)) {
			LED = rotate_LED(1, 1, LED);
			pressed_buttons[1] = false;
		}
		if (bit_is_clear(PIND,PIND2) && (pressed_buttons[2] == true)) {
			LED = rotate_LED(-1, 2, LED);
			pressed_buttons[2] = false;
		}		
		if (bit_is_clear(PIND,PIND3) && (pressed_buttons[3] == true)) {
			LED = rotate_LED(1, 2, LED);
			pressed_buttons[3] = false;
		}
		if (bit_is_clear(PIND,PIND4) && (pressed_buttons[4] == true)) {
			LED = 0x01;
			pressed_buttons[4] = false;
		}	
	}
}

