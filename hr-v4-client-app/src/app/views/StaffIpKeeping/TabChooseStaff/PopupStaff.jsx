import {Button, ButtonGroup, DialogContent, Grid} from "@material-ui/core";
import {observer} from "mobx-react";
import React, {memo} from "react";
import SalaryStaffsToolbar from "./PopupStaffToolbar";
import {store} from "../../../stores";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";
import SalaryStaffsList from "./PopupStaffList";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";

function PopupStaff() {
    const {popupStaffStore} = store;
    const {handleClose, openCreateEditPopup} = popupStaffStore;

    return (
        <GlobitsPopupV2
            size="lg"
            open={openCreateEditPopup}
            noDialogContent
            title={"Danh sách nhân viên"}
            onClosePopup={handleClose}
        >
            <DialogContent className="o-hidden p-12">
                <SalaryStaffsToolbar/>
                <SalaryStaffsList/>
                <Grid item xs={12}
                      style={{display: "flex", justifyContent: "end"}}
                >
                    <ButtonGroup
                        color="container"
                        aria-label="outlined primary button group"
                    >
                        <Button
                            type="button"
                            onClick={handleClose}
                            startIcon={<HighlightOffIcon/>}
                        >
                            Đóng danh sách chọn nhân viên
                        </Button>
                    </ButtonGroup>
                </Grid>
            </DialogContent>
        </GlobitsPopupV2>
    );
}

export default memo(observer(PopupStaff));

