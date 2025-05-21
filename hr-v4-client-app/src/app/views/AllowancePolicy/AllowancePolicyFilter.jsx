import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { useStore } from 'app/stores';
import { observer } from 'mobx-react';
import { memo } from 'react';
import { useTranslation } from 'react-i18next';
import { pagingAllowance } from '../Allowance/AllowanceService';
import { pagingAllDepartments } from '../Department/DepartmentService';
import { pagingPosition } from '../Position/PositionService';
import { pagingAllOrg } from '../Organization/OrganizationService';

function AllowancePolicyFilter(props) {
    const { allowancePolicyStore } = useStore();
    const { t } = useTranslation();

    const {
        searchObject,
        intactSearchObject
    } = allowancePolicyStore;

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
                                        name="position"
                                        label={"Vị trí"}
                                        api={pagingPosition}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name="allowance"
                                        label={"Phụ cấp"}
                                        api={pagingAllowance}
                                    //getOptionLabel={(option) => `${option?.name}`}
                                    />
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
                </Grid>
            </div>

        </Collapse>
    );
}

export default memo(observer(AllowancePolicyFilter));