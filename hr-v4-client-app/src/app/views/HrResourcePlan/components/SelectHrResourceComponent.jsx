import { Button, ButtonGroup, Grid, Table, TableBody, TableCell, TableHead, TableRow } from "@material-ui/core";
import { memo, useState } from "react";
// import "./RequestStyle.scss";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import TouchAppIcon from "@material-ui/icons/TouchApp";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import SelectMultipleHrResourcesPopup from "./SelectMultipleHrResources/SelectMultipleHrResourcesPopup";

function SelectHrResourceComponent(props) {
    const {
        organizationId = null,
        clearFields = [],
        name = "childrenPlans",
        label = "Định biên",
        disabled = false,
        disabledTextFieldOnly = false,
        required = false,
    } = props;

    const { handleOpenCreateEdit, handleSelectListDelete } = useStore().hrResourcePlanStore;
    const { t } = useTranslation();

    const { values, setFieldValue } = useFormikContext();

    const [openDepartmentPopup, setOpenDepartmentPopup] = useState(false);

    function handelOpenDepartmentPopup() {
        setOpenDepartmentPopup(true);
    }

    function handleClosePopup() {
        setOpenDepartmentPopup(false);
    }

    const handleDeleteByIndex = (rowData) => {
        let newChildren = values?.childrenPlans?.filter((item) => item.id !== rowData?.id);
        setFieldValue("childrenPlans", newChildren || []);
    };

    let columns = [
        {
            title: t("general.action"),
            width: "6%",
            align: "center",
            render: (rowData, rows, index) => {
                return (
                    <div className='flex flex-middle justify-center'>
                        <Tooltip title='Xóa' placement='top'>
                            <IconButton size='small' className='ml-4' onClick={() => handleDeleteByIndex(rowData)}>
                                <Icon fontSize='small' color='secondary'>
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        {
            title: t("Tên định biên"),
            field: "name",
            align: "left",
        },
        {
            title: t("Mã định biên"),
            field: "code",
            align: "left",
        },
        {
            title: t("Phòng ban"),
            field: "department.name",
            align: "left",
            render: (rowData) => <span className='pr-6'>{rowData?.department?.name}</span>,
        },
    ];

    const handleOpenChoice = () => {
        handelOpenDepartmentPopup(true);

        if (values?.childrenPlans) {
            handleSelectListDelete(values?.childrenPlans);
        } else {
            handleSelectListDelete([]);
        }
    };

    return (
        // <div className="input-popup-container">
        <Grid container spacing={2}>
            <Grid item xs={3}>
                <ButtonGroup color='container' aria-label='outlined primary button group'>
                    <Button startIcon={<TouchAppIcon />} onClick={() => handleOpenChoice()}>
                        Chọn vị các định biên
                    </Button>
                </ButtonGroup>
            </Grid>
            <Grid item xs={12}>
                <GlobitsTable data={values?.childrenPlans} columns={columns} />
            </Grid>
            {openDepartmentPopup && (
                <SelectMultipleHrResourcesPopup open={openDepartmentPopup} setOpen={setOpenDepartmentPopup} />
            )}
        </Grid>
    );
}

export default memo(observer(SelectHrResourceComponent));
