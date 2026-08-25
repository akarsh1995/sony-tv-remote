package com.sonyremote.app.data.model

/**
 * Sony Bravia IRCC-IP Command definitions.
 * IRCC (InfraRed Compatible Control) codes are Base64 encoded strings
 * accepted by Sony Bravia TVs at the `/sony/ircc` endpoint.
 */
enum class IrccCommand(val displayName: String, val code: String) {
    // Power Controls
    POWER("Power Toggle", "AAAAAQAAAAEAAAAVAw=="),
    POWER_ON("Power On", "AAAAAQAAAAEAAAAuAw=="),
    POWER_OFF("Power Off", "AAAAAQAAAAEAAAAvAw=="),

    // Navigation Controls
    UP("Up", "AAAAAQAAAAEAAAB0Aw=="),
    DOWN("Down", "AAAAAQAAAAEAAAB1Aw=="),
    LEFT("Left", "AAAAAQAAAAEAAAA0Aw=="),
    RIGHT("Right", "AAAAAQAAAAEAAAAzAw=="),
    ENTER("Enter / OK", "AAAAAQAAAAEAAABlAw=="),
    HOME("Home", "AAAAAQAAAAEAAABgAw=="),
    BACK("Back / Return", "AAAAAgAAAJcAAAAjAw=="),
    EXIT("Exit", "AAAAAQAAAAEAAABjAw=="),
    OPTIONS("Options", "AAAAAgAAAJcAAAA2Aw=="),

    // Volume & Audio
    VOLUME_UP("Volume +", "AAAAAQAAAAEAAAASAw=="),
    VOLUME_DOWN("Volume -", "AAAAAQAAAAEAAAATAw=="),
    MUTE("Mute", "AAAAAQAAAAEAAAAUAw=="),

    // Channel Controls
    CHANNEL_UP("Channel +", "AAAAAQAAAAEAAAAQAw=="),
    CHANNEL_DOWN("Channel -", "AAAAAQAAAAEAAAARAw=="),
    PREV_CHANNEL("Previous Channel", "AAAAAQAAAAEAAAA7Aw=="),

    // Source Inputs
    INPUT("Input", "AAAAAQAAAAEAAAAlAw=="),
    HDMI1("HDMI 1", "AAAAAgAAABoAAABaAw=="),
    HDMI2("HDMI 2", "AAAAAgAAABoAAABbAw=="),
    HDMI3("HDMI 3", "AAAAAgAAABoAAABcAw=="),
    HDMI4("HDMI 4", "AAAAAgAAABoAAABdAw=="),

    // Media Playback
    PLAY("Play", "AAAAAgAAAJcAAAAaAw=="),
    PAUSE("Pause", "AAAAAgAAAJcAAAAZAw=="),
    STOP("Stop", "AAAAAgAAAJcAAAAYAw=="),
    REWIND("Rewind", "AAAAAgAAAJcAAAAbAw=="),
    FORWARD("Fast Forward", "AAAAAgAAAJcAAAAcAw=="),
    PREV("Previous Track", "AAAAAgAAAJcAAAA8Aw=="),
    NEXT("Next Track", "AAAAAgAAAJcAAAA9Aw=="),

    // Dedicated App Shortcuts
    NETFLIX("Netflix", "AAAAAgAAABoAAAB8Aw=="),
    YOUTUBE("YouTube", "AAAAAgAAAMQAAABHAw=="),
    PRIME_VIDEO("Prime Video", "AAAAAgAAABoAAAB+Aw=="),
    GOOGLE_PLAY("Google Play", "AAAAAgAAAMQAAABGAw=="),

    // Numeric Keys
    NUM_0("0", "AAAAAQAAAAEAAAAJAw=="),
    NUM_1("1", "AAAAAQAAAAEAAAAAAw=="),
    NUM_2("2", "AAAAAQAAAAEAAAABAw=="),
    NUM_3("3", "AAAAAQAAAAEAAAACAw=="),
    NUM_4("4", "AAAAAQAAAAEAAAADAw=="),
    NUM_5("5", "AAAAAQAAAAEAAAAEAw=="),
    NUM_6("6", "AAAAAQAAAAEAAAAFAw=="),
    NUM_7("7", "AAAAAQAAAAEAAAAGAw=="),
    NUM_8("8", "AAAAAQAAAAEAAAAHAw=="),
    NUM_9("9", "AAAAAQAAAAEAAAAIAw=="),
    DOT(".", "AAAAAgAAAJcAAAAdAw=="),

    // Color Keys (Teletext / Smart TV Interactive)
    RED("Red", "AAAAAgAAAJcAAAAlAw=="),
    GREEN("Green", "AAAAAgAAAJcAAAAmAw=="),
    YELLOW("Yellow", "AAAAAgAAAJcAAAAnAw=="),
    BLUE("Blue", "AAAAAgAAAJcAAAAkAw=="),

    // Display & Accessibility
    SUBTITLE("Subtitle", "AAAAAgAAAJcAAAAoAw=="),
    DISPLAY("Display Info", "AAAAAQAAAAEAAAA6Aw=="),
    EPG("Guide / EPG", "AAAAAgAAAKQAAABbAw==")
}
