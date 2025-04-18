 # SHIELD – Android Personal Security Tool
 
 **SHIELD** is an always-on Android security app designed for your Motorola Edge 2024, built with custom EDGEX.xml integration. It protects against phone cloning, unauthorized access, USB debugging, unknown app installs, and RF-based interference.
 
 ## Features
 
 - Persistent background service with notification ("SHIELD IS ACTIVE")
 - Startup at boot, always-on
 - Phone clone detection (IMEI/SIM monitoring)
 - Blocks USB debugging & unknown source installs
 - Destroys hostile monitoring behavior
 - EDGEX.xml configuration loader
 - Monitors for suspicious Accessibility Services
 - Local VPN-based ad/pop-up suppression
 
 ## Installation
 
 1. Enable "Install unknown apps" for your browser or file manager
 2. Download and install the latest APK from Releases
 3. App runs in background automatically
 
 ## Permissions Required
 
 - `RECEIVE_BOOT_COMPLETED`
 - `FOREGROUND_SERVICE`
 - `PACKAGE_USAGE_STATS`
 - `INTERNET`
 - `ACCESS_NETWORK_STATE`
 - `QUERY_ALL_PACKAGES`
 - `REQUEST_DELETE_PACKAGES`
 - Accessibility & Device Admin privileges (optional, for full features)
 
 ## Built With
 
 - Kotlin + Java (Code Studio IDE)
 - Android SDK 31+
 - GitHub Actions ready
 
 ---
 
 ### Developer
 
 GitHub: [@98919891](https://github.com/98919891)  
 Built with ChatGPT in Code Studio
