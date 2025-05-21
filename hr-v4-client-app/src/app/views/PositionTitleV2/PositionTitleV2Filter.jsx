import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import { pagingParentPositionTitle } from "../PositionTitle/PositionTitleService";
import { pagingRankTitle } from "../RankTitle/RankTitleService";

function PositionTitleV2Filter(props) {
    const { t } = useTranslation();

    const { positionTitleV2Store } = useStore();
    const { intactSearchObject } = positionTitleV2Store;

    const { isOpenFilter, handleFilter, handleCloseFilter } = props;

    const { resetForm } = useFormikContext();

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
        };
        handleFilter(newSearchObject);
        resetForm();
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
                                        name='rankTitle'
                                        label={"Cấp bậc"}
                                        api={pagingRankTitle}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name='parent'
                                        label={"Nhóm ngạch"}
                                        api={pagingParentPositionTitle}
                                    />
                                </Grid>
                                {/* <Grid item xs={12} sm={4}>
                                    <GlobitsPagingAutocompleteV2
                                        name="positionRole"
                                        label={"Nhóm quyền mặc định"}
                                        api={pagingPositionRole}
                                    />
                                </Grid> */}
                                {/* <Grid item xs={12} sm={4}>
                                    <GlobitsSelectInput
                                        label={"Loại vị trí"}
                                        name="type"
                                        options={LocalConstants.ListPositionTitleType}
                                    />
                                </Grid> */}
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

export default memo(observer(PositionTitleV2Filter));
