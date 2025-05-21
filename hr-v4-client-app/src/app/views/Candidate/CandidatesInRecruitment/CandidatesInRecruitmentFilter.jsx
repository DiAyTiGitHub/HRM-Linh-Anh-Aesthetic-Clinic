import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { observer } from 'mobx-react';
import React, { memo, useEffect } from 'react';
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import { useFormikContext } from 'formik';
import { useTranslation } from 'react-i18next';
import { useStore } from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import SearchIcon from '@material-ui/icons/Search';
import { pagingPosition } from 'app/views/Position/PositionService';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import { pagingRecruitment } from 'app/views/Recruitment/Recruitment/RecruitmentService';

function CandidatesInRecruitmentFilter(props) {
    const { candidateStore } = useStore();
    const {
        searchObject,
        intactSearchObject
    } = candidateStore;

    const {
        handleCloseFilter,
        isOpenFilter,
        handleFilter
    } = props;

    const { t } = useTranslation();
    const { values, setFieldValue, setValues, handleReset, handleSubmit } = useFormikContext();

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize,
        };
        handleFilter(newSearchObject);
    }

    return (
        <Collapse in={isOpenFilter} className="filterPopup">
            <div className="flex flex-column">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="filterContent pt-8">
                            <Grid container spacing={2}>

                                {/* <Grid item xs={12} lg={6}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Thông tin tuyển dụng
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Đợt tuyển dụng")}
                                                name="recruitment"
                                                api={pagingRecruitment}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("Vị trí ứng tuyển")}
                                                name="position"
                                                api={pagingPosition}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid> */}

                                <Grid item xs={12} lg={6}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Ngày nộp hồ sơ trong khoảng
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsDateTimePicker
                                                label="Nộp hồ sơ từ ngày"
                                                name="submissionDateFrom"
                                                disableFuture
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={6}>
                                            <GlobitsDateTimePicker
                                                label="Đến ngày"
                                                disableFuture
                                                name="submissionDateTo"
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <div className="py-8 mt-12 border-bottom-fade border-top-fade">
                                <div className="flex justify-end" >
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            onClick={handleResetFilter}
                                            startIcon={<RotateLeftIcon />}
                                        >
                                            Đặt lại
                                        </Button>
                                        <Button
                                            type="button"
                                            onClick={handleCloseFilter}
                                            startIcon={<HighlightOffIcon />}
                                        >
                                            Đóng bộ lọc
                                        </Button>
                                    </ButtonGroup>
                                </div>
                            </div>
                        </div>
                    </Grid>
                </Grid >
            </div >

        </Collapse >
    );
}

export default memo(observer(CandidatesInRecruitmentFilter));