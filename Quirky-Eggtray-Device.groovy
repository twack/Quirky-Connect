/**
 *  Quirky Eggtray
 *
 *  Author: todd@wackford.net
 *  Date: 2014-02-22
 *
 *****************************************************************
 *     Setup Namespace, capabilities, attributes and commands
 *****************************************************************
 * Namespace:			"wackford"
 *
 * Capabilities:		"polling"
 *						"refresh"
 *
 * Custom Attributes:	"inventory"
 *						"totalEggs"
 *						"freshEggs"
 *						"oldEggs"
 *						"eggReport"
 *
 * Custom Commands:		"eggReport"
 *
 *****************************************************************
 *                       Changes
 *****************************************************************
 *  Change 1:	2014-02-26
 *				Added egg report
 *				implemented icons/tiles (thanks to Dane)
 *
 *  Change 2:	2014-03-10
 *				Documented Header
 *
 *  Change 3:	2014-09-30
 *				added child uninstall call to parent
 *
 *****************************************************************
 *                       Code
 *****************************************************************
 */
 
 // for the UI
metadata {
	// Automatically generated. Make future change here.
	definition (name: "Quirky Eggtray", namespace: "wackford", author: "todd@wackford.net", oauth: true) {
		capability "Refresh"
		capability "Polling"

		attribute "inventory", "string"
		attribute "totalEggs", "string"
		attribute "freshEggs", "string"
		attribute "oldEggs", "string"
		attribute "eggReport", "string"

		command "eggReport"
	}


	tiles {
		standardTile("inventory", "device.inventory", width: 2, height: 2){
        	state "goodEggs", label : "    ", unit : "" , icon:"st.quirky.egg-minder.quirky-egg-device", backgroundColor: "#53a7c0"
            state "haveBadEgg", label : "    ", unit : "" , icon:"st.quirky.egg-minder.quirky-egg-device", backgroundColor: "#FF1919"
            state "noEggs", label : "    ", unit : "" , icon:"st.quirky.egg-minder.quirky-egg-device", backgroundColor: "#ffffff"
        }
        standardTile("totalEggs", "device.totalEggs", inactiveLabel: false){
        	state "totalEggs", label : '${currentValue}', unit : "" , icon:"st.quirky.egg-minder.quirky-egg-count", backgroundColor: "#53a7c0"
        } 
        standardTile("freshEggs", "device.freshEggs", inactiveLabel: false){
        	state "freshEggs", label : '${currentValue}', unit : "" , icon:"st.quirky.egg-minder.quirky-egg-fresh", backgroundColor: "#53a7c0"
        }
        standardTile("oldEggs", "device.oldEggs", inactiveLabel: false){
        	state "oldEggs", label : '${currentValue}', unit : "" , icon:"st.quirky.egg-minder.quirky-egg-expired", backgroundColor: "#53a7c0"
        }
        standardTile("eggReport", "device.eggReport", inactiveLabel: false, decoration: "flat"){
        	state "eggReport", action: "eggReport", label : '    ', unit : "" , icon:"st.quirky.egg-minder.quirky-egg-report"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}     
	}

	main(["inventory", "totalEggs", "freshEggs", "oldEggs"])
    details(["inventory", "eggReport", "refresh", "totalEggs", "freshEggs", "oldEggs"])
    
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

def eggReport() {
	log.debug "Executing Egg Report"
    parent.runEggReport(this)
}

def uninstalled() {
	log.debug "Executing 'uninstall' in child"
    parent.uninstallChildDevice(this)
}

def poll() {
	log.debug "Executing 'poll'"
	parent.getEggtrayUpdate(this)
}

def refresh() {
	log.debug "Executing 'refresh'"
	parent.getEggtrayUpdate(this)
}
