import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
// import PositionCUForm from "../PositionCUForm";
import MulPositionsList from "./MulPositionsList";
import MulPositionToolbar from "./MulPositionToolbar";

function MulPositionsPopup(props) {
    const { t } = useTranslation();

    const { isDisabled, currentPositions, open, handleClose } = props;

    const { positionStore, departmentStore, positionTitleStore } = useStore();

    const {
        searchObject,
        pagingPosition,
        resetStore,
        handleCloseConfirmAssignPopup,
        handleOpenConfirmAssignPopup,
        openCreateEditPopup,
    } = positionStore;

    const { selectedDepartment, handleSelectDepartment } = departmentStore;

    const { selectedPositionTitle, handleSelectPositionTitle } = positionTitleStore;
    useEffect(() => {
        console.log("department:", selectedDepartment);
        searchObject.department = selectedDepartment;
        searchObject.departmentId = selectedDepartment?.id;
        searchObject.positionTitle = selectedPositionTitle;
        searchObject.positionTitleId = selectedPositionTitle?.id;
        pagingPosition();

        return resetStore;
    }, [selectedDepartment?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='md'
            open={open}
            noDialogContent
            title={"Danh sách vị trí"}
            onClosePopup={handleClose}>
            <>
                <div className='dialog-body'>
                    <DialogContent className='p-12'>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <MulPositionToolbar />
                            </Grid>
                            <Grid item xs={12}>
                                <MulPositionsList />
                            </Grid>
                        </Grid>
                    </DialogContent>
                </div>

                <div className='dialog-footer dialog-footer-v2 py-8'>
                    <DialogActions className='p-0'>
                        <div className='flex flex-space-between flex-middle'>
                            <Button
                                startIcon={<BlockIcon />}
                                variant='contained'
                                className='mr-12 btn btn-secondary d-inline-flex'
                                color='secondary'
                                onClick={handleClose}>
                                {t("general.button.cancel")}
                            </Button>
                            <Button
                                startIcon={<SaveIcon />}
                                className='mr-0 btn bgc-lighter-dark-blue d-inline-flex'
                                variant='contained'
                                color='primary'
                                onClick={handleOpenConfirmAssignPopup}>
                                Xác nhận
                            </Button>
                        </div>
                    </DialogActions>
                </div>

                {/* {openCreateEditPopup && <PositionCUForm handleAfterSubmit={pagingPosition} />} */}
            </>
        </GlobitsPopupV2>
    );
}

export default memo(observer(MulPositionsPopup));
