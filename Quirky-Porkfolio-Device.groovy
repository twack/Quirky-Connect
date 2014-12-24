 /* Quirky Porkfolio
 *
 *  Author: todd@wackford.net
 *  Date: 2014-02-22
 *
 *****************************************************************
 *     Setup Namespace, acpabilities, attributes and commands
 *****************************************************************
 * Namespace:			"wackford"
 *
 * Capabilities:		"acceleration"
 *						"battery"
 *						"polling"
 *						"refresh"
 *
 * Custom Attributes:	"balance"
 *						"goal"
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
 *  Change 2:	2014-09-30
 *				Added child device uninstall call to parent
 *
 *****************************************************************
 *                       Code
 *****************************************************************
 */
metadata {
	// Automatically generated. Make future change here.
	definition (name: "Quirky Porkfolio", namespace: "wackford", author: "todd@wackford.net", oauth: true) {
		capability "Battery"
		capability "Refresh"
		capability "Polling"
        capability "Acceleration Sensor"

		attribute "balance", "string"
		attribute "goal", "string"
	}

	tiles {
    	standardTile("acceleration", "device.acceleration", width: 2, height: 2, canChangeIcon: true) {
        	state "inactive", label:'pig secure', icon:"st.quirky.porkfolio.quirky-porkfolio-side"//, backgroundColor:"#44b621"
			state "active", label:'pig alarm', icon:"st.quirky.porkfolio.quirky-porkfolio-dead", backgroundColor:"#FF1919"	
		}
        standardTile("balance", "device.balance", inactiveLabel: false, canChangeIcon: true) {
			state "balance", label:'${currentValue}', unit:"", icon:"st.quirky.porkfolio.quirky-porkfolio-facing"
		}
        standardTile("goal", "device.goal", inactiveLabel: false, decoration: "flat", canChangeIcon: true) {
			state "goal", label:'${currentValue} goal', unit:"", icon:"st.quirky.porkfolio.quirky-porkfolio-success"
		}
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
	}
	main(["acceleration", "balance"])
    details(["acceleration", "balance", "goal", "refresh" ])
}

// parse events into attributes
def parse(description) {
	log.debug "parse() - $description"
	def results = []

	if (description?.name && description?.value)
	{
		results << sendEvent(name: "${description?.name}", value: "${description?.value}")
	}
}

def uninstalled() {
	log.debug "Executing 'uninstall' in child"
    parent.uninstallChildDevice(this)
}

def poll() {
	log.debug "Executing 'poll'"
	parent.getPiggyBankUpdate(this)
}

def refresh() {
	log.debug "Executing 'refresh'"
	parent.getPiggyBankUpdate(this)
}
