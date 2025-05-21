import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import StaffCertificateFilter from "./StaffCertificateFilter";

function StaffCertificateToolbar() {
    const {
        staffCertificateStore,
        hrRoleUtilsStore
    } = useStore();

    const { t } = useTranslation();

    const {
        pagingPersonCertificate,
        searchObject,
        handleSetSearchObject,
        listChosen,
        handleDeleteList,
        handleOpenCreateEdit,
    } = staffCertificateStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingPersonCertificate();
    }

    //const { id } = useParams();

    const [isOpenFilter, setIsOpenFilter] = useState(true);

    function handleCloseFilter() {
        if (isOpenFilter) {
            setIsOpenFilter(false);
        }
    }

    function handleOpenFilter() {
        if (!isOpenFilter) {
            setIsOpenFilter(true);
        }
    }

    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    const {
        isAdmin,
        isManager,
    } = hrRoleUtilsStore;

    let canApproveOrUnApprove = false;

    if (isAdmin || isManager) {
        canApproveOrUnApprove = true;
    }

    return (
        <Formik
            enableReinitialize
            initialValues={searchObject}
            onSubmit={handleFilter}
        >
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete='off'>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} lg={6}>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        {(canApproveOrUnApprove) && (
                                            <Tooltip arrow placement='top' title='Tạo lần chấm công thủ công'>
                                                <Button startIcon={<AddIcon />} onClick={() => handleOpenCreateEdit()}>
                                                    {t("general.button.add")}
                                                </Button>
                                            </Tooltip>
                                        )}

                                        {(canApproveOrUnApprove) && (
                                            <Button
                                                disabled={listChosen?.length === 0}
                                                startIcon={<DeleteOutlineIcon />}
                                                onClick={handleDeleteList}>
                                                {t("general.button.delete")}
                                            </Button>
                                        )}

                                    </ButtonGroup>

                                </Grid>

                                <Grid item xs={12} lg={6}>
                                    <div className='flex justify-between align-center'>
                                        <Tooltip placement='top' title='Tìm kiếm theo tên nhân viên'>
                                            <GlobitsTextField
                                                placeholder='Tìm kiếm theo tên nhân viên'
                                                name='keyword'
                                                variant='outlined'
                                                notDelay
                                            />
                                        </Tooltip>

                                        <ButtonGroup
                                            className='filterButtonV4'
                                            color='container'
                                            aria-label='outlined primary button group'>
                                            <Button
                                                startIcon={<SearchIcon className={``} />}
                                                className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                                type='submit'>
                                                Tìm kiếm
                                            </Button>
                                            <Button
                                                startIcon={
                                                    <FilterListIcon
                                                        className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                                                    />
                                                }
                                                className=' d-inline-flex py-2 px-8 btnHrStyle'
                                                onClick={handleTogglePopupFilter}>
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>

                                <Grid item xs={12}>
                                    <StaffCertificateFilter
                                        isOpenFilter={isOpenFilter}
                                        handleFilter={handleFilter}
                                        handleCloseFilter={handleCloseFilter}
                                        resetForm={resetForm}
                                    />
                                </Grid>
                            </Grid>


                        </Grid>

                    </Form>
                );
            }}
        </Formik >
    );
}

export default memo(observer(StaffCertificateToolbar));
