/*
 * WiiM Amp Controller Hubitat App
 *
 * This Hubitat user app allows you to send play and pause commands to a WiiM Amp on your local network.
 *
 * To use: Install this app in Hubitat, enter your WiiM Amp's IP and port, and use the app UI to send play/pause commands.
 */

definition(
        name: "WiiM Amp Controller",
        namespace: "yourNamespace",
        author: "Your Name",
        description: "Control WiiM Amp (play/pause) via local network.",
        category: "Convenience",
        iconUrl: "",
        iconX2Url: ""
)

preferences {
    page(name: "mainPage")
    appButtonHandler: "appButtonHandler"
}

def mainPage() {
    dynamicPage(name: "mainPage", title: "WiiM Amp Controller", install: true, uninstall: true) {
        section("WiiM Amp Settings") {
            input "ampIps", "text", title: "WiiM Amp IP Address(es) (comma-separated)", required: true
            input "ampPort", "number", title: "WiiM Amp Port", defaultValue: 443, required: true
        }
        section("Virtual Button Device") {
            input "buttonDevice", "capability.pushableButton", title: "Select Virtual Button Device", required: false, multiple: false
        }
        section("Actions") {
            input "playBtn", "button", title: "Play"
            input "pauseBtn", "button", title: "Pause"
        }
    }
}

// Button event handler for UI buttons
def appButtonHandler(btn) {
    if (btn == "playBtn") {
        sendCommandToAmp("play")
        log.info "Play button pressed from app UI."
    } else if (btn == "pauseBtn") {
        sendCommandToAmp("pause")
        log.info "Pause button pressed from app UI."
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    if (settings.buttonDevice) {
        subscribe(settings.buttonDevice, "pushed", buttonEventHandler)
    }
}

def buttonEventHandler(evt) {
    log.debug "Button event: ${evt.value} (button ${evt.data})"
    // Button 1 = Play, Button 2 = Pause (customize as needed)
    if (evt.value == "pushed") {
        if (evt.data && evt.data.contains('"buttonNumber":2')) {
            pauseHandler()
        } else {
            playHandler()
        }
    }
}

def playHandler() {
    sendCommandToAmp("play")
}

def pauseHandler() {
    sendCommandToAmp("pause")
}

def sendCommandToAmp(String command) {
    def cmd = command == "play" ? "setPlayerCmd:play" : "setPlayerCmd:pause"
    def ips = settings.ampIps.tokenize(',').collect { it.trim() }
    ips.each { ip ->
        def url = "https://${ip}/httpapi.asp?command=${cmd}"
        log.debug "Attempting to send '${command}' command to URL: ${url}"
        try {
            httpGet([uri: url, ignoreSSLIssues: true]) { resp ->
                log.debug "Response status: ${resp.status}"
                log.debug "Response data: ${resp.data}"
                if (resp.status == 200) {
                    log.info "${command.capitalize()} command sent successfully to ${ip}."
                } else {
                    log.warn "Failed to send ${command} command to ${ip}. Response: ${resp.status}"
                }
            }
        } catch (Exception e) {
            log.error("Error sending ${command} command to ${ip}: ${e.message}")
        }
    }
}
