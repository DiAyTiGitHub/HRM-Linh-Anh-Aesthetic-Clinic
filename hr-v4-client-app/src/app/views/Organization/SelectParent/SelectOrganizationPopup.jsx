import React , { memo , useState } from "react";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import Draggable from "react-draggable";
import {
    Button ,
    Dialog ,
    DialogContent ,
    DialogTitle ,
    Grid ,
    Icon ,
    IconButton ,
    Tooltip ,
} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import SelectOrganizationList from "./SelectOrganizationList";
import TouchAppIcon from '@material-ui/icons/TouchApp';

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

function SelectOrganizationPopup(props) {
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
                    variant="contained"
                    fullWidth
                    // color="primary"
                    className="btn bgc-lighter-dark-blue text-white d-inline-flex my-2"

                    onClick={() => setOpenChooseParentPopup(true)}
                    disabled={readOnly}
                >
                    <TouchAppIcon className="text-white"/>
                </Button>
            </Tooltip>


            {openChooseParentPopup && (
                <Dialog
                    className="dialog-container"
                    open={openChooseParentPopup}
                    PaperComponent={PaperComponent}
                    fullWidth
                    maxWidth="md"
                >
                    <DialogTitle
                        className="dialog-header bgc-primary"
                        style={{cursor:"move"}}
                        id="draggable-dialog-title"
                    >
                        <span className="mb-20 text-white">{t("department.select")}</span>
                    </DialogTitle>
                    <IconButton
                        className="text-white"
                        style={{position:"absolute" , right:"0" , top:"0"}}
                        onClick={handleClosePopup}
                    >
                        <Icon title={t("general.close")}>
                            close
                        </Icon>
                    </IconButton>
                    <DialogContent className="p-12" style={{overflowY:"auto" , maxHeight:"75vh"}}>
                        <Grid container spacing={2}>
                            <Grid item lg={6} md={6} sm={4} xs={4}></Grid>
                            <Grid item lg={6} md={6} sm={8} xs={8}>
                                <GlobitsSearchInput search={updatePageData}/>
                            </Grid>

                            <Grid item xs={12}>
                                <SelectOrganizationList
                                    handleClose={handleClosePopup}
                                />
                            </Grid>
                        </Grid>

                    </DialogContent>
                </Dialog>
            )}

        </>
    );
}

export default memo(observer(SelectOrganizationPopup));
