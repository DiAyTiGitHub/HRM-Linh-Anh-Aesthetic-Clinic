import {Button, ButtonGroup, Collapse, Grid} from '@material-ui/core';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import {observer} from 'mobx-react';
import React, {memo} from 'react';
import {useTranslation} from 'react-i18next';
import {useStore} from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';

import { useFormikContext } from 'formik';
import { pagingAllOrg } from 'app/views/Organization/OrganizationService';
import { pagingAllDepartments } from 'app/views/Department/DepartmentService';
import { pagingPositionTitle } from 'app/views/PositionTitle/PositionTitleService';
import { pagingRankTitle } from 'app/views/RankTitle/RankTitleService';
import { pagingStaff } from 'app/views/HumanResourcesInformation/StaffService';
import GlobitsCheckBox from 'app/common/form/GlobitsCheckBox';

function PositionFilter(props) {
    const {positionStore} = useStore();
    const {t} = useTranslation();

    const {
        intactSearchObject
    } = positionStore;

    const {
        isOpenFilter,
        handleFilter,
        handleCloseFilter
    } = props;

    const {resetForm} = useFormikContext();

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
        };
        handleFilter(newSearchObject);
        resetForm();
    }

    return (
        <Collapse in={isOpenFilter} className="filterPopup">
            <div className="flex flex-column">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="filterContent pt-8">
                            <Grid container spacing={2} className={"flex flex-end"}>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name="organization"
                                        label={"Đơn vị"}
                                        api={pagingAllOrg}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name="department"
                                        label={"Phòng ban"}
                                        api={pagingAllDepartments}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name="positionTitle"
                                        label={"Chức danh"}
                                        api={pagingPositionTitle}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name="rankTitle"
                                        label={"Cấp bậc"}
                                        api={pagingRankTitle}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name="staff"
                                        label={"Nhân viên"}
                                        api={pagingStaff}
                                        getOptionLabel={
                                            (option) => `${option?.displayName}` + ' - ' + `${option?.staffCode}`
                                        }
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsCheckBox
                                        label={"Vị trí còn trống"}
                                        name='isVacant'
                                    />
                                </Grid>
                            </Grid>

                            <div className="py-8 mt-12 border-bottom-fade border-top-fade">
                                <div className="flex  justify-end">
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

export default memo(observer(PositionFilter));