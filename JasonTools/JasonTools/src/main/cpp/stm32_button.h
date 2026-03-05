#ifndef STM32_BUTTON_H
#define STM32_BUTTON_H

#ifdef __cplusplus
extern "C" {
#endif

/* Define STM32_BUTTON_HAL_HEADER to your STM32 HAL header before including. */
#ifndef STM32_BUTTON_HAL_HEADER
#define STM32_BUTTON_HAL_HEADER "stm32f1xx_hal.h"
#endif

#include STM32_BUTTON_HAL_HEADER
#include <stdint.h>

/* Call stm32_button_update() every 1-10ms; set debounce_ms to at least 2-3x the poll interval. */
typedef enum {
    STM32_BUTTON_EVENT_NONE = 0,
    STM32_BUTTON_EVENT_PRESSED,
    STM32_BUTTON_EVENT_RELEASED,
    STM32_BUTTON_EVENT_LONG_PRESSED
} stm32_button_event_t;

typedef struct {
    GPIO_TypeDef *port;
    uint16_t pin;
    GPIO_PinState active_state;
    uint32_t debounce_ms;
    uint32_t long_press_ms;
    GPIO_PinState stable_state;
    GPIO_PinState last_read_state;
    uint32_t last_transition_ms;
    uint32_t pressed_ms;
    uint8_t long_press_reported;
} stm32_button_t;

/* Passing NULL for button or port is safe; helpers become no-ops or return inactive state. */
void stm32_button_init(stm32_button_t *button,
                       GPIO_TypeDef *port,
                       uint16_t pin,
                       GPIO_PinState active_state,
                       uint32_t debounce_ms,
                       uint32_t long_press_ms);

stm32_button_event_t stm32_button_update(stm32_button_t *button);

uint8_t stm32_button_is_pressed(const stm32_button_t *button);

#ifdef __cplusplus
}
#endif

#endif
