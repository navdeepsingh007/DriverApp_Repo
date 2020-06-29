package com.seasia.driverapp.common

import android.view.View

interface DialogsInterface {
    fun onDialogConfirmAction(mView : View?, mKey : String)
    fun onDialogCancelAction(mView : View?, mKey : String)
}