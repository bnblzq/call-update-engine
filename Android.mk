LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

# This is the target being built.
LOCAL_PACKAGE_NAME := Helloworld

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
#LOCAL_STATIC_JAVA_LIBRARIES += android-support-v13

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
LOCAL_RESOURCE_DIR += prebuilts/sdk/current/support/v7/appcompat/res
# Only compile source java files in this apk.
LOCAL_SRC_FILES := $(call all-java-files-under, java)
LOCAL_CERTIFICATE := platform
#LOCAL_STATIC_JAVA_LIBRARIES := libandroidsupportv4_dbt
# Link against the current Android SDK.
# LOCAL_SDK_VERSION := current
LOCAL_PROGUARD_FLAG_FILES := proguard-rules.pro

LOCAL_PRIVILEGED_MODULE := true
LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat:android.support.v7.gridlayout
include $(BUILD_PACKAGE)


