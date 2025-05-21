import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { observer } from 'mobx-react';
import React, { memo } from 'react';
import { useTranslation } from 'react-i18next';
import { useStore } from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import { pagingAllowance } from '../Allowance/AllowanceService';
import { pagingAllOrg } from '../Organization/OrganizationService';
import { pagingAllDepartments } from '../Department/DepartmentService';
import { pagingPosition } from '../Position/PositionService';
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import LocalConstants from "../../LocalConstants";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";

function StaffPositionFilter(props) {
    const { t } = useTranslation();

    const { staffPositionStore } = useStore();
    const {
        searchObject,
        intactSearchObject
    } = staffPositionStore;


    const {
        isOpenFilter,
        handleFilter,
        handleCloseFilter
    } = props;

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
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name="department"
                                        label={"Phòng ban"}
                                        api={pagingAllDepartments}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsSelectInput
                                        label={t("positionStaff.relationshipType")}
                                        name="relationshipType"
                                        options={LocalConstants.RelationshipType.getListData()}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name="position"
                                        label={"Vị trí"}
                                        api={pagingPosition}
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
                                    <GlobitsPagingAutocompleteV2
                                        name="supervisor"
                                        label={"Nguời quản lý"}
                                        api={pagingStaff}
                                        getOptionLabel={
                                            (option) => `${option?.displayName}` + ' - ' + `${option?.staffCode}`
                                        }
                                    />
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
                </Grid>
            </div>

        </Collapse>
    );
}

export default memo(observer(StaffPositionFilter));