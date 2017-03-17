LICENSE = "CLOSED"

DEPENDS += "wpewebkit glib-2.0"

SRCREV = "92fc38421847aafdb75d73c7566d29ca3c2c3997"

SRC_URI = "git://github.com/WebPlatformForEmbedded/WPEBackend-rdk.git;protocol=http;branch=master"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

# Default back end selections. Please override in your machine config using WPE_BACKEND=<> to meet your machine required
WPE_BACKEND ?= "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'westeros', 'rpi', d)}"
WPE_BACKEND_append = "${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", " wayland","", d)}"
WPE_BACKEND_remove = "${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "westeros","", d)}"

PACKAGECONFIG ?= "${WPE_BACKEND}"

# device specific backends
PACKAGECONFIG[intelce] = "-DUSE_WPEWEBKIT_BACKEND_INTEL_CE=ON -DUSE_HOLE_PUNCH_GSTREAMER=ON -DUSE_KEY_INPUT_HANDLING_LINUX_INPUT=ON,,intelce-display"
PACKAGECONFIG[nexus] = "-DUSE_WPEWEBKIT_BACKEND_BCM_NEXUS=ON -DUSE_HOLE_PUNCH_GSTREAMER=ON -DUSE_KEY_INPUT_HANDLING_LINUX_INPUT=ON,,broadcom-refsw"
PACKAGECONFIG[rpi] = "-DUSE_WPEWEBKIT_BACKEND_BCM_RPI=ON -DUSE_KEY_INPUT_HANDLING_LINUX_INPUT=ON,,userland"
PACKAGECONFIG[stm] = "-DUSE_WPEWEBKIT_BACKEND_STM=ON -DUSE_KEY_INPUT_HANDLING_LINUX_INPUT=OFF -DUSE_HOLE_PUNCH_GSTREAMER=ON,,libxkbcommon"

# Wayland selectors
PACKAGECONFIG[wayland] = "-DUSE_WPEWEBKIT_BACKEND_WAYLAND=ON -DUSE_KEY_INPUT_HANDLING_LINUX_INPUT=OFF,,wayland libxkbcommon"
PACKAGECONFIG[westeros] = "-DUSE_WPEWEBKIT_BACKEND_WESTEROS=ON -DUSE_WPEWEBKIT_BACKEND_BCM_RPI=OFF -DUSE_KEY_INPUT_HANDLING_LINUX_INPUT=OFF -DUSE_HOLE_PUNCH_GSTREAMER=ON -DUSE_WESTEROS_SINK=ON,,wayland westeros libxkbcommon"
PACKAGECONFIG[bcm-weston] = "-DUSE_WPEWEBKIT_BACKEND_BCM_NEXUS_WAYLAND=ON,-DUSE_WPEWEBKIT_BACKEND_BCM_NEXUS_WAYLAND=OFF,,"

do_install() {

	install -d ${D}${libdir}
	install -m 0755 ${B}/libWPEBackend-*.so ${D}${libdir}/

}

FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/libWPEBackend-default.so ${libdir}/libWPEBackend-rdk.so"
INSANE_SKIP ="dev-so"