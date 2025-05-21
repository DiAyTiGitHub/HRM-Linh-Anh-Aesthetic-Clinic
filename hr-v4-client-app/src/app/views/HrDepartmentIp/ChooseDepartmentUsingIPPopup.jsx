import React , { memo , useEffect , useState } from "react";
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
import DepartmentFilters from "app/views/Department/DepartmentFilters";
import ChooseDepartmentUsingIpList from "./ChooseDepartmentUsingIpList";
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

function ChooseDepartmentUsingIPPopup({readOnly}) {
    const {t} = useTranslation();

    const [openPopup , setOpenPopup] = useState(false);

    const handleConfirmSelectDepartment = () => {
        setOpenPopup(false);
    };

    function handleClosePopup() {
        setOpenPopup(false);
    }

    return (
        <>
            <Tooltip placement="top" title={t("general.button.select") + " đơn vị"}>
                <Button
                    variant="contained"
                    fullWidth
                    className="btn bgc-lighter-dark-blue text-white mt-25 px-0"
                    onClick={() => setOpenPopup(true)}
                    disabled={readOnly}
                >
                    <TouchAppIcon className="text-white"/>
                </Button>
            </Tooltip>

            {openPopup && (

                <GlobitsPopupV2
                    size="md"
                    scroll={"body"}
                    open={openPopup}
                    noDialogContent
                    title={t("Lựa chọn đơn vị")}
                    onClosePopup={handleClosePopup}
                    popupId={"popupselectdep"}
                >
                    <DialogContent
                        className="o-hidden p-12"
                        // style={{ maxHeight: "80vh" }}
                    >
                        <Grid container className="mb-16">
                            <Grid item lg={6} md={6} sm={4} xs={4}></Grid>
                            <Grid item lg={6} md={6} sm={8} xs={8}>
                                <DepartmentFilters/>
                            </Grid>
                        </Grid>
                        <Grid item xs={12}>
                            <ChooseDepartmentUsingIpList
                                handleClose={handleClosePopup}
                            />
                        </Grid>
                    </DialogContent>
                </GlobitsPopupV2>
            )}
        </>
    );
}

export default memo(observer(ChooseDepartmentUsingIPPopup));
