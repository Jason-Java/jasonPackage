#include "stm32_button.h"

/* uint32_t subtraction handles HAL_GetTick() wrap-around. */
static uint32_t stm32_button_elapsed(uint32_t now_ms, uint32_t last_ms) {
    return now_ms - last_ms;
}

void stm32_button_init(stm32_button_t *button,
                       GPIO_TypeDef *port,
                       uint16_t pin,
                       GPIO_PinState active_state,
                       uint32_t debounce_ms,
                       uint32_t long_press_ms) {
    if (button == NULL) {
        return;
    }

    button->port = port;
    button->pin = pin;
    button->active_state = active_state;
    button->debounce_ms = debounce_ms;
    button->long_press_ms = long_press_ms;
    button->stable_state = HAL_GPIO_ReadPin(port, pin);
    button->last_read_state = button->stable_state;
    button->last_transition_ms = HAL_GetTick();
    button->pressed_ms = button->last_transition_ms;
    button->long_press_reported = 0;
}

stm32_button_event_t stm32_button_update(stm32_button_t *button) {
    GPIO_PinState read_state;
    uint32_t now_ms;

    if (button == NULL) {
        return STM32_BUTTON_EVENT_NONE;
    }

    now_ms = HAL_GetTick();
    read_state = HAL_GPIO_ReadPin(button->port, button->pin);

    if (read_state != button->last_read_state) {
        button->last_read_state = read_state;
        button->last_transition_ms = now_ms;
    }

    if (read_state != button->stable_state &&
        stm32_button_elapsed(now_ms, button->last_transition_ms) >= button->debounce_ms) {
        button->stable_state = read_state;
        if (read_state == button->active_state) {
            button->pressed_ms = now_ms;
            button->long_press_reported = 0;
            return STM32_BUTTON_EVENT_PRESSED;
        }
        return STM32_BUTTON_EVENT_RELEASED;
    }

    if (button->stable_state == button->active_state &&
        button->long_press_ms > 0 &&
        !button->long_press_reported &&
        stm32_button_elapsed(now_ms, button->pressed_ms) >= button->long_press_ms) {
        button->long_press_reported = 1;
        return STM32_BUTTON_EVENT_LONG_PRESSED;
    }

    return STM32_BUTTON_EVENT_NONE;
}

uint8_t stm32_button_is_pressed(const stm32_button_t *button) {
    if (button == NULL) {
        return 0;
    }

    return button->stable_state == button->active_state ? 1 : 0;
}
