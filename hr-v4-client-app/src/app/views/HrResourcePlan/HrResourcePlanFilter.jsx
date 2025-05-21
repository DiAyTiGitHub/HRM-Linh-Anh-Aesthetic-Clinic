import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import { pagingAllDepartments } from "../Department/DepartmentService";
import { pagingAllOrg } from "../Organization/OrganizationService";
import { useFormikContext } from "formik";

function HrResourcePlanFilter(props) {
    const { hrResourcePlanStore } = useStore();
    const { t } = useTranslation();

    const { searchObject, intactSearchObject, resetSearch } = hrResourcePlanStore;

    const { isOpenFilter, handleFilter, handleCloseFilter } = props;

    const { resetForm, values } = useFormikContext();

    function handleResetFilter() {
        resetSearch();
        resetForm();
    }

    return (
        <Collapse in={isOpenFilter} className='filterPopup'>
            <div className='flex flex-column'>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className='filterContent pt-8'>
                            <Grid container spacing={2} className={"flex flex-end"}>
                                <Grid item xs={12} sm={3}>
                                    <GlobitsPagingAutocompleteV2
                                        name='organization'
                                        label={"Đơn vị"}
                                        api={pagingAllOrg}
                                        getOptionLabel={(option) => `${option?.name}`}
                                        value={values.organization}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={3}>
                                    <GlobitsPagingAutocompleteV2
                                        name='department'
                                        label={"Phòng ban"}
                                        api={pagingAllDepartments}
                                        getOptionLabel={(option) => `${option?.name}`}
                                        value={values.department}
                                    />
                                </Grid>

                                {/*<Grid item md={3}>*/}
                                {/*    <GlobitsDateTimePicker name='fromDate' label='Từ ngày' />*/}
                                {/*</Grid>*/}

                                {/*<Grid item md={3}>*/}
                                {/*    <GlobitsDateTimePicker name='toDate' label='Đến ngày' />*/}
                                {/*</Grid>*/}
                            </Grid>

                            <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                                <div className='flex justify-end'>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        <Button onClick={handleResetFilter} startIcon={<RotateLeftIcon />}>
                                            Đặt lại
                                        </Button>
                                        <Button
                                            type='button'
                                            onClick={handleCloseFilter}
                                            startIcon={<HighlightOffIcon />}>
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

export default memo(observer(HrResourcePlanFilter));
