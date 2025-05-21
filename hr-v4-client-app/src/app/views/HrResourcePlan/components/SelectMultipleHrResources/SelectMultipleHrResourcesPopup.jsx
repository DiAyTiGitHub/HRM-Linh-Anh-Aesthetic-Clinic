import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import SelectMultipleHrResourcesList from "./SelectMultipleHrResourcesList";
import SelectMultipleHrResourcesToolbar from "./SelectMultipleHrResourcesToolbar";

function SelectMultipleHrResourcesPopup(props) {
    const { t } = useTranslation();

    const { open, setOpen, isDisabled, currentPositions } = props;
    const { setFieldValue } = useFormikContext();
    const { hrResourcePlanStore } = useStore();

    const {
        listForSelect,
        pagingHrResourcePlan,
        getListHrResourcePlan,
        resetStoreNonClose,
        handleCloseConfirmAddHrResourcePlanPopup,
        handleOpenConfirmAddHrResourcePlanPopup,
        openConfirmAddHrResourcePlanPopup,
        setListForSelect,
        listOnDelete,
    } = hrResourcePlanStore;

    useEffect(() => {
        getListHrResourcePlan() // Truyền giá trị phù hợp
            .then((data) => {
                console.log(data);
                setListForSelect(data);
            })
            .catch((err) => {
                console.log(err);
            });
        return resetStoreNonClose;
    }, []);

    async function handleConfirmAssign() {
        try {
            handleCloseConfirmAddHrResourcePlanPopup();
            handleClose();
        } catch (error) {
            console.error(error);
        }
    }

    const handleClose = () => {
        setOpen(false);
    };

    const handleSaveForm = () => {
        console.log(listOnDelete);
        setFieldValue("childrenPlans", listOnDelete);
        handleClose();
    };
    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='md'
            open={open}
            noDialogContent
            title={"Lựa chọn các định biên"}
            onClosePopup={handleClose}>
            <>
                <div className='dialog-body'>
                    <DialogContent className='p-12'>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <SelectMultipleHrResourcesToolbar />
                            </Grid>
                            <Grid item xs={12}>
                                <SelectMultipleHrResourcesList />
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
                                type='submit'
                                onClick={handleSaveForm}
                                color='primary'>
                                Thêm
                            </Button>
                        </div>
                    </DialogActions>
                </div>
            </>
        </GlobitsPopupV2>
    );
}

export default memo(observer(SelectMultipleHrResourcesPopup));
