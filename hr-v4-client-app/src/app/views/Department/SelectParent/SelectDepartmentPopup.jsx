import React , { memo , useEffect , useState } from "react";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useFormikContext } from "formik";
import Draggable from "react-draggable";
import {
    Dialog ,
    DialogTitle ,
    Icon ,
    IconButton ,
    DialogContent ,
    Grid ,
    DialogActions ,
    Button ,
    Tooltip ,
} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import SelectDepartmentList from "./SelectDepartmentList";
import TouchAppIcon from '@material-ui/icons/TouchApp';
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

function PaperComponent(props) {
    return (
        <Draggable
            handle="#draggable-dialog-title"
            cancel={'[class*="MuiDialogContent-root"]'}
        >
            <Paper {... props} />
        </Draggable>
    );
}

function SelectDepartmentPopup(props) {
    const {readOnly} = props;
    const {t} = useTranslation();
    const {departmentStore} = useStore();

    const {handleToggleDepartmentPopup , updatePageData} = departmentStore;
    const [openChooseParentPopup , setOpenChooseParentPopup] = useState(false);

    function handleClosePopup() {
        setOpenChooseParentPopup(false);
    }

    return (
        <>
            <Tooltip placement="top" title={t("general.button.select") + " đơn vị"}>
                <Button
                    fullWidth
                    variant="contained"
                    className="btn bgc-lighter-dark-blue text-white mt-25 px-0 w-100"
                    onClick={() => setOpenChooseParentPopup(true)}
                    disabled={readOnly}
                >
                    <TouchAppIcon className="text-white"/>

                </Button>
            </Tooltip>

            {openChooseParentPopup && (
                <GlobitsPopupV2
                    size="md"
                    scroll={"body"}
                    open={openChooseParentPopup}
                    noDialogContent
                    title={t("department.select")}
                    onClosePopup={handleClosePopup}
                    popupId={"select-parent"}
                >
                    <DialogContent className="p-12" style={{overflowY:"auto" , maxHeight:"88vh"}}>
                        <Grid container spacing={2}>
                            <Grid item lg={6} md={6} sm={4} xs={4}></Grid>
                            <Grid item lg={6} md={6} sm={8} xs={8}>
                                <GlobitsSearchInput search={updatePageData}/>
                            </Grid>

                            <Grid item xs={12}>
                                <SelectDepartmentList
                                    handleClose={handleClosePopup}
                                />
                            </Grid>
                        </Grid>

                    </DialogContent>

                </GlobitsPopupV2>
            )}


        </>
    );
}

export default memo(observer(SelectDepartmentPopup));
