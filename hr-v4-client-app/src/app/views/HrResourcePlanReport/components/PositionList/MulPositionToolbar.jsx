import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import PositionFilter from "./PositionFilter";

function SelectMulPositionToolbar() {
    const { positionStore, staffStore } = useStore();
    const { t } = useTranslation();

    const { pagingPosition, searchObject, handleSetSearchObject, handleOpenCreateFormWithStaff } = positionStore;

    const { getStaff } = staffStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
            pageSize: searchObject.pageSize,
        };
        handleSetSearchObject(newSearchObject);
        await pagingPosition();
    }

    const [isOpenFilter, setIsOpenFilter] = useState(false);

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

    const { id } = useParams();

    return (
        <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete='off'>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <ButtonGroup
                                        className='filterButtonV4'
                                        color='container'
                                        aria-label='outlined primary button group'>
                                        <Button
                                            startIcon={<AddIcon />}
                                            className='d-inline-flex py-2 px-8 btnHrStyle'
                                            onClick={async () => {
                                                let staff = null;
                                                if (id) {
                                                    staff = await getStaff(id);
                                                }
                                                handleOpenCreateFormWithStaff(staff);
                                            }}>
                                            Thêm mới vị trí
                                        </Button>
                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <div className='flex justify-between align-center'>
                                        <Tooltip placement='top' title='Tìm kiếm theo tên vị trí'>
                                            <GlobitsTextField
                                                placeholder='Tìm kiếm theo tên vị trí...'
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
                            </Grid>

                            <PositionFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            />
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(SelectMulPositionToolbar));
