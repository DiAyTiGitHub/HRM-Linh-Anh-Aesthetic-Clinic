import React, {memo} from "react";
import {Form, Formik, useFormikContext} from "formik";
import {Button, ButtonGroup, DialogActions, DialogContent, Grid, Icon, IconButton} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import {useTranslation} from "react-i18next";
import {useStore} from "../../stores";
import {observer} from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";
import PopupStaff from "./TabChooseStaff/PopupStaff";
import {formatDate} from "../../LocalFunction";
import GlobitsTable from "../../common/GlobitsTable";
import AddIcon from "@material-ui/icons/Add";

function StaffIpKeepingForm() {
    const {staffIpKeepingStore, popupStaffStore} = useStore();
    const {t} = useTranslation();

    const {
        openCreateEditPopup, handleClose, saveExternalIpTimekeeping, handleSetSearchObject
    } = staffIpKeepingStore;

    async function handleSaveForm(values) {
        await saveExternalIpTimekeeping(values);
    }

    return (<GlobitsPopupV2
            scroll={"body"}
            size="lg"
            open={openCreateEditPopup}
            noDialogContent
            title={t("general.button.add") + ' ' + t("navigation.staffIpKeeping.title")}
            onClosePopup={() => {
                handleClose()
            }}
        >
            <Formik
                enableReinitialize
                onSubmit={handleSaveForm}
                initialValues={{selectedListStaffIpKeeping: []}}>
                {({isSubmitting, values, setFieldValue, initialValues}) => {

                    return (<Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError/>

                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <ButtonGroup
                                                color="container"
                                                aria-label="outlined primary button group"
                                                style={{marginBottom: 15}}
                                            >
                                                <Button
                                                    startIcon={<AddIcon/>}
                                                    onClick={() => popupStaffStore.handleOpen()}
                                                >
                                                    Chọn nhân viên thực hiện
                                                </Button>
                                            </ButtonGroup>
                                            <ChooseStaffList/>
                                            <PopupStaff/>
                                        </Grid>
                                    </Grid>

                                </DialogContent>
                            </div>

                            <div className="dialog-footer py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
                                        <Button
                                            startIcon={<BlockIcon/>}
                                            variant="contained"
                                            className="mr-12 btn btn-secondary d-inline-flex"
                                            color="secondary"
                                            onClick={() => {
                                                handleClose()
                                            }}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon/>}
                                            className="mr-0 btn btn-primary d-inline-flex"
                                            variant="contained"
                                            color="primary"
                                            type="submit"
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.save")}
                                        </Button>
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>);
                }}
            </Formik>
        </GlobitsPopupV2>);
}

export default memo(observer(StaffIpKeepingForm));


const ChooseStaffList = observer(() => {
    const {t} = useTranslation();
    const {values, setFieldValue} = useFormikContext();

    const handleRemoveSelectedStaff = (chosenStaff) => {
        let valuesNew = values?.selectedListStaffIpKeeping.filter(item => String(item.id) !== String(chosenStaff?.id));
        setFieldValue("selectedListStaffIpKeeping", valuesNew)
    };

    const columns = [{
        title: t("general.action"), minWidth: "48px", align: "center", render: (rowData) => (<IconButton
                size="small"
                onClick={(event) => {
                    event.stopPropagation();
                    handleRemoveSelectedStaff(rowData);
                }}
            >
                <Icon fontSize="small" color="secondary">
                    delete
                </Icon>
            </IconButton>),
    }, {
        title: "Mã nhân viên",
        field: "staffCode",
        align: "center",
        render: (rowData) => <span className='px-6'>{rowData?.staffCode}</span>,
    }, {
        title: "Nhân viên", minWidth: "200px", render: (rowData) => (<>
            {rowData.displayName && (<p className='m-0'>
                <strong>{rowData.displayName}</strong>
            </p>)}

            {rowData.birthDate && <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>}

            {rowData.gender && <p className='m-0'>Giới
                tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>}

            {rowData.birthPlace && <p className='m-0'>Nơi sinh: {rowData.birthPlace}</p>}
        </>),
    }, {
        title: "Thông tin liên hệ", field: "info", minWidth: "200px", render: (rowData) => (<>
            {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}

            {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}
        </>),
    },

        {
            title: "Đơn vị đang công tác",
            field: "organization.name",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.organization?.name}</span>,
        },

        {
            title: "Phòng ban",
            field: "department.name",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.department?.name}</span>,
        },

        {
            title: "Vị trí",
            field: "currentPosition.name",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.currentPosition?.name}</span>,
        },

        {
            title: "Nơi ở hiện tại",
            field: "currentResidence",
            align: "left",
            minWidth: "180px",
            render: (rowData) => <span className='pr-6'>{rowData?.currentResidence}</span>,
        },];

    return (<GlobitsTable
        data={values?.selectedListStaffIpKeeping || []}
        columns={columns}
        nonePagination
    />);
})