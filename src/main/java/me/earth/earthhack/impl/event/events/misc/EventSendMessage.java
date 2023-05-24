package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;

public class EventSendMessage extends Event {
    private String message;

    public EventSendMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
