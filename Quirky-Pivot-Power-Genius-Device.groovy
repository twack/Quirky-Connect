/*  Quirky Pivot Power Genius
 *
 *  Author: todd@wackford.net
 *  Date: 2014-01-28
 *
 *****************************************************************
 *     Setup Namespace, acpabilities, attributes and commands
 *****************************************************************
 * Namespace:			"wackford"
 *
 * Capabilities:		"switch"
 *						"polling"
 *						"refresh"
 *
 * Custom Attributes:	"none"
 *
 * Custom Commands:		"none"
 *
 *****************************************************************
 *                       Changes
 *****************************************************************
 *
 *  Change 1:	2014-03-10
 *				Documented Header
 *
 *****************************************************************
 *                       Code
 *****************************************************************
 */
 // for the UI
metadata {
	// Automatically generated. Make future change here.
	definition (name: "Quirky Pivot Power Genius", namespace: "wackford", author: "todd@wackford.net", oauth: true) {
		capability "Refresh"
		capability "Polling"
        capability "Switch"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: '${name}', action: "switch.on", icon: "st.quirky.pivot-genius.quirky-pivot-off", backgroundColor: "#ffffff"
            state "on", label: '${name}', action: "switch.off", icon: "st.quirky.pivot-genius.quirky-pivot-on", backgroundColor: "#79b821"	
		}
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
	}
	main(["switch"])
    details(["switch", "refresh" ])
}


// parse events into attributes
def parse(description) {
	log.debug "parse() - $description"
	def results = []
	
    if ( description == "updated" ) on initial install we are returned just a string
    	return
        
	if (description?.name && description?.value)
	{
		results << sendEvent(name: "${description?.name}", value: "${description?.value}")
	}
}


// handle commands
def on() {
	log.debug "Executing 'on'"
    log.debug this
	parent.on(this)
}

def off() {
	log.debug "Executing 'off'"
	parent.off(this)
}

def uninstalled() {
	log.debug "Executing 'uninstall' in child"
    parent.uninstallChildDevice(this)
}

def poll() {
	log.debug "Executing 'poll'"
	parent.pollOutlet(this)
}

def refresh() {
	log.debug "Executing 'refresh'"
	parent.pollOutlet(this)
}



