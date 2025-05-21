import {Button, ButtonGroup, Collapse, Grid} from '@material-ui/core';
import {observer} from 'mobx-react';
import React, {memo} from 'react';
import {useTranslation} from 'react-i18next';
import {useStore} from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import GlobitsDateTimePicker from "../../common/form/GlobitsDateTimePicker";
import {useFormikContext} from "formik";

function ProjectFilter(props) {
    const {staffIpKeepingStore} = useStore();
    const {t} = useTranslation();
    const {resetForm, setFieldValue, values} = useFormikContext()
    const {
        intactSearchObject,
    } = staffIpKeepingStore;
    const {
        isOpenFilter,
        handleFilter,
        handleCloseFilter
    } = props;


    function handleResetFilter() {
        resetForm();
    }

    function hanleChangeCheckbox({target}) {
        setFieldValue("isFinished", target?.checked);
    }

    return (
        <Collapse in={isOpenFilter} className="filterPopup">
            <div className="flex flex-column">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="filterContent pt-8">
                            <Grid container spacing={2} className={"flex flex-end"}>
                                <Grid item xs={12} md={3}>
                                    <GlobitsDateTimePicker
                                        label="Ngày bắt đầu"
                                        name="startDate"
                                    />
                                </Grid>
                                <Grid item xs={12} md={3}>
                                    <GlobitsDateTimePicker
                                        label="Ngày kết thúc"

                                        name="endDate"
                                    />
                                </Grid>
                                <Grid item xs={12} md={3}
                                      className="align-center pt-20 flex"
                                >
                                    <input
                                        type="checkbox"
                                        name="isFinished"
                                        checked={values?.isFinished}
                                        onChange={hanleChangeCheckbox}
                                    />
                                    <p className="pl-8 filterField m-0">
                                        Tìm kiếm dự án đã kết thúc
                                    </p>
                                </Grid>
                            </Grid>
                            <div className="py-8 mt-12 border-bottom-fade border-top-fade">
                                <div className="flex justify-end">
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            onClick={handleResetFilter}
                                            startIcon={<RotateLeftIcon/>}
                                        >
                                            Đặt lại
                                        </Button>
                                        <Button
                                            type="button"
                                            onClick={handleCloseFilter}
                                            startIcon={<HighlightOffIcon/>}
                                        >
                                            Đóng bộ lọc
                                        </Button>
                                    </ButtonGroup>
                                </div>
                            </div>
                        </div>
                    </Grid>
                </Grid>
            </div>
        </Collapse>
    );
}

export default memo(observer(ProjectFilter));