/**
 *  Quirky Refuel
 *
 *  Copyright 2014 Todd Wackford
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Quirky Refuel", namespace: "wackford", author: "Todd Wackford", oauth: true) {
		capability "Polling"
		capability "Refresh"
        capability "Battery"
		capability "Sensor"
        
        attribute "tankLevel", "string"
		attribute "tankChanged", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
        standardTile("refresh", "device.sensor", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        //standardTile("acceleration", "device.acceleration", width: 2, height: 2, canChangeIcon: true) {
		//	state "active", label:'vibration', icon:"st.quirky.spotter.quirky-spotter-main", backgroundColor:"#53a7c0"
		//	state "inactive", label:'still', icon:"st.quirky.spotter.quirky-spotter-main", backgroundColor:"#ffffff"
		//}
        standardTile("tankLevel", "device.tankLevel", inactiveLabel: false, width: 2, height: 2, canChangeIcon: true) {
			state("tankLevel", label:'${currentValue}%', unit:'',icon:"st.quirky.refuel.refuel",
				backgroundColors:[
					[value: 0,  color: "#FF1919"],
                    [value: 12, color: "#ffe71e"],
                    [value: 25, color: "#79b821"]
					
				]
			)
		}
        valueTile("tankChanged", "device.tankChanged", inactiveLabel: false, decoration: "flat") {
			state "tankChanged", label:'Changed ${currentValue}', unit:""
		}
        valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
			state "battery", label: '${currentValue}% battery'
		}
        
    	main "tankLevel"
		details(["tankLevel", "battery", "refresh"]) //not using tank changed display is wonky across devices
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "parse() - $description"
	def results = []
    
    if ( description == "updated" )
    	return

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
    parent.pollPropaneTank(this)
}

def refresh() {
	log.debug "Executing 'refresh'"
    parent.pollPropaneTank(this)
}


