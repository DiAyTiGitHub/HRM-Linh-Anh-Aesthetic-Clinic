import { Button , ButtonGroup , Collapse , Grid } from '@material-ui/core';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { useStore } from 'app/stores';
import { pagingRecruitment } from 'app/views/Recruitment/Recruitment/RecruitmentService';
import { useFormikContext } from 'formik';
import { observer } from 'mobx-react';
import { memo } from 'react';
import { useTranslation } from 'react-i18next';
import { pagingAllOrg } from "../../../Organization/OrganizationService";
import { pagingAllDepartments } from "../../../Department/DepartmentService";
import { pagingPositionTitle } from "../../../PositionTitle/PositionTitleService";

function CandidateFilter({handleFilter}) {
    const {candidateStore} = useStore();
    const {
        searchObject ,
        intactSearchObject ,
        isOpenFilter ,
        setIsOpenFilter ,
    } = candidateStore;


    const {t} = useTranslation();
    const {resetForm} = useFormikContext();

    function handleResetFilter() {
        const newSearchObject = {
            ... JSON.parse(JSON.stringify(intactSearchObject)) ,
            pageIndex:searchObject.pageIndex ,
            pageSize:searchObject.pageSize ,
        };
        resetForm();
        handleFilter(newSearchObject);
    }

    return (<Collapse in={isOpenFilter} className="filterPopup">
        <div className="flex flex-column">
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <div className="filterContent pt-8">
                        <Grid container spacing={2}>

                            <Grid item xs={12}>
                                <Grid container spacing={2} className={"flex flex-end"}>
                                    <Grid item xs={12} className="pb-0">
                                        <p className="m-0 p-0 borderThrough2">
                                            Thông tin tuyển dụng
                                        </p>
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={3}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("Đơn vị tuyển dụng")}
                                            name="organization"
                                            api={pagingAllOrg}
                                        />
                                    </Grid>
                                    <Grid item xs={12} sm={6} md={3}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("Phòng ban tuyển dụng")}
                                            name="department"
                                            api={pagingAllDepartments}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={3}>
                                        <GlobitsPagingAutocompleteV2
                                            label={"Chức danh cần tuyển"}
                                            validate
                                            name="positionTitle"
                                            api={pagingPositionTitle}
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={3}>
                                        <GlobitsPagingAutocompleteV2
                                            label={t("Đợt tuyển dụng")}
                                            name="recruitment"
                                            api={pagingRecruitment}
                                        />
                                    </Grid>

                                </Grid>
                            </Grid>

                            <Grid item xs={12}>
                                <Grid container spacing={2} className={"flex flex-end"}>
                                    <Grid item xs={12} className="pb-0">
                                        <p className="m-0 p-0 borderThrough2">
                                            Khoảng thời gian nộp hồ sơ
                                        </p>
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={3}>
                                        <GlobitsDateTimePicker
                                            label="Nộp hồ sơ từ ngày"
                                            name="submissionDateFrom"
                                            disableFuture
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6} md={3}>
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
                                        onClick={() => {
                                            setIsOpenFilter(false)
                                        }}
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
    </Collapse>);
}

export default memo(observer(CandidateFilter));