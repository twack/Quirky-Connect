 Welcome back, todd@wackford.net
Logout
My Locations
My Hubs
My Devices
My SmartApps
My Device Types
My Publication Requests
Live Logging
Documentation
Device Type Templates 
Location

Set Location
Quirky Eggtray Save  Publish   IDE Settings  Device Type Settings

1
/**
2
 *  Quirky Eggtray
3
 *
4
 *  Author: todd@wackford.net
5
 *  Date: 2014-02-22
6
 *
7
 *****************************************************************
8
 *     Setup Namespace, capabilities, attributes and commands
9
 *****************************************************************
10
 * Namespace:           "wackford"
11
 *
12
 * Capabilities:        "polling"
13
 *                      "refresh"
14
 *
15
 * Custom Attributes:   "inventory"
16
 *                      "totalEggs"
17
 *                      "freshEggs"
18
 *                      "oldEggs"
19
 *                      "eggReport"
20
 *
21
 * Custom Commands:     "eggReport"
22
 *
23
 *****************************************************************
24
 *                       Changes
25
 *****************************************************************
26
 *  Change 1:   2014-02-26
27
 *              Added egg report
28
 *              implemented icons/tiles (thanks to Dane)
29
 *
30
 *  Change 2:   2014-03-10
31
 *              Documented Header
32
 *
33
 *  Change 3:   2014-09-30
34
 *              added child uninstall call to parent
35
 *
36
 *****************************************************************
37
 *                       Code
38
 *****************************************************************
39
 */
40
 
41
 // for the UI
42
metadata {
43
    // Automatically generated. Make future change here.
44
    definition (name: "Quirky Eggtray", namespace: "wackford", author: "todd@wackford.net", oauth: true) {
45
        capability "Refresh"
