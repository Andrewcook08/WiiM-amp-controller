package org.example

class WiiMAmpController {
    String ampIp
    int port

    WiiMAmpController(String ampIp, int port) {
        this.ampIp = ampIp
        this.port = port
    }

    boolean play() {
        return sendCommand('play')
    }

    boolean pause() {
        return sendCommand('pause')
    }

    private boolean sendCommand(String command) {
        try {
            def url = new URL("http://${ampIp}:${port}/api/amp/${command}")
            def conn = url.openConnection()
            conn.requestMethod = 'POST'
            conn.connect()
            int responseCode = conn.responseCode
            return responseCode == 200
        } catch (Exception e) {
            e.printStackTrace()
            return false
        }
    }
}

