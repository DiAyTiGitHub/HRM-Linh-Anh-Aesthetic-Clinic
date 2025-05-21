import {Button, ButtonGroup, Collapse, Grid, useFormControl} from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import {useStore} from "app/stores";
import {pagingStaff} from "app/views/HumanResourcesInformation/StaffService";
import {pagingAllOrg} from "app/views/Organization/OrganizationService";
import {pagingPositionTitle} from "app/views/PositionTitle/PositionTitleService";
import {pagingRankTitle} from "app/views/RankTitle/RankTitleService";
import {useFormikContext} from "formik";
import {observer} from "mobx-react";
import {memo} from "react";
import {useTranslation} from "react-i18next";

function PositionFilter(props) {
    const {positionStore} = useStore();
    const {t} = useTranslation();

    const {intactSearchObject} = positionStore;

    const {isOpenFilter, handleFilter, handleCloseFilter} = props;

    const {values} = useFormikContext();

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            department: values?.department || "",
        };

        handleFilter(newSearchObject);
    }

    return (
        <Collapse in={isOpenFilter} className='filterPopup'>
            <div className='flex flex-column'>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className='filterContent pt-8'>
                            <Grid container spacing={2} className={"flex flex-end"}>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name='organization'
                                        label={"Tổ chức"}
                                        api={pagingAllOrg}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name='staff'
                                        label={"Nhân viên"}
                                        api={pagingStaff}
                                        getOptionLabel={(option) =>
                                            `${option?.displayName}` + " - " + `${option?.staffCode}`
                                        }
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name='positionTitle'
                                        label={"Chức danh"}
                                        api={pagingPositionTitle}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name='rankTitle'
                                        label={"Cấp bậc"}
                                        api={pagingRankTitle}
                                    />
                                </Grid>
                            </Grid>
                            <div className='py-8 mt-12 border-bottom-fade border-top-fade'>
                                <div className='flex justify-end'>
                                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                                        <Button onClick={handleResetFilter} startIcon={<RotateLeftIcon/>}>
                                            Đặt lại
                                        </Button>
                                        <Button
                                            type='button'
                                            onClick={handleCloseFilter}
                                            startIcon={<HighlightOffIcon/>}>
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
