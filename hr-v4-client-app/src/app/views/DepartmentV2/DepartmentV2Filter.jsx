import { Button, ButtonGroup, Collapse, Grid } from '@material-ui/core';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import { useStore } from 'app/stores';
import { observer } from 'mobx-react';
import React, { memo } from 'react';
import { useTranslation } from 'react-i18next';
import GlobitsCheckBox from "../../common/form/GlobitsCheckBox";
import { useFormikContext } from "formik";
import GlobitsPagingAutocomplete from 'app/common/form/GlobitsPagingAutocomplete';
import { pagingDepartmentType } from '../DepartmentType/DepartmentTypeService';

function DepartmentV2Filter(props) {
    const { departmentV2Store } = useStore();
    const { t } = useTranslation();

    const {
        searchObject,
        intactSearchObject
    } = departmentV2Store;

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

    const { values } = useFormikContext();
    return (
        <Collapse in={isOpenFilter} className="filterPopup">
            <div className="flex flex-column">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="filterContent pt-8">
                            <Grid
                                container
                                spacing={2}
                            className="flex flex-end"
                            >
                                {/* <Grid item xs={12}>
                                    <p className="m-0 p-0 borderThrough2">Bộ lọc</p>
                                </Grid> */}

                                <Grid item xs={12} md={4} lg={3}>
                                    <GlobitsCheckBox
                                        label={"Cần bổ nhiệm quản lý"}
                                        name='isManager'
                                        checked={values?.isManager}
                                    />
                                </Grid>

                                <Grid item xs={12} md={4} lg={3}>
                                    <GlobitsPagingAutocomplete
                                        name='hrDepartmentType'
                                        label={t("department.hrdepartmentType")}
                                        api={pagingDepartmentType}
                                        getOptionLabel={(option) =>
                                            option?.name && option?.sortNumber
                                                ? `${option.name} - Cấp ${option.sortNumber}`
                                                : option?.name || option?.sortNumber || ""
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

export default memo(observer(DepartmentV2Filter));