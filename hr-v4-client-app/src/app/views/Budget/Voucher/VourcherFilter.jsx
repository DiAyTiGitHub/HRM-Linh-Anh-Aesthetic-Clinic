import {Button, ButtonGroup, Collapse, Grid} from '@material-ui/core';
import {observer} from 'mobx-react';
import React, {memo} from 'react';
import {useFormikContext} from 'formik';
import {useTranslation} from 'react-i18next';
import {useStore} from 'app/stores';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import GlobitsSelectInput from "../../../common/form/GlobitsSelectInput";
import {pagingBudget} from "../Budget/BudgetService";
import GlobitsDateTimePicker from "../../../common/form/GlobitsDateTimePicker";
import {pagingStaff} from "../../HumanResourcesInformation/StaffService";

const voucherTypeOptions = [
    {value: 1, name: "Thu"},
    {value: -1, name: "Chi"},
];

function VourcherFilter(props) {
    const {voucherStore} = useStore();
    const {
        searchObject,
    } = voucherStore;

    const {
        handleCloseFilter,
        isOpenFilter,
        handleFilter
    } = props;

    const {t} = useTranslation();
    const {values, setFieldValue, setValues, handleReset, handleSubmit} = useFormikContext();

    async function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(searchObject)),
            keyword: "",
            fromDate: null,
            toDate: null,
            voucherType: null,
            budget: null,
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
                                <Grid item xs={12} className="pb-0">
                                    <p className="m-0 p-0 borderThrough2">
                                        {/* Thông tin bảo hiểm */}
                                        {/* Bộ lọc */}
                                    </p>
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2} className='justify-end'>
                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsSelectInput
                                                label={t("voucher.voucherType")}
                                                name='voucherType'
                                                keyValue='value'
                                                options={voucherTypeOptions}/>
                                        </Grid>
                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                label={"Ngân sách"}
                                                name="budget"
                                                api={pagingBudget}
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsDateTimePicker
                                                label="Từ ngày"
                                                name="fromDate"
                                                placeholder="Từ ngày"
                                            />
                                        </Grid>
                                        <Grid item xs={12} sm={6} md={3} lg={3}>
                                            <GlobitsDateTimePicker
                                                label="Đến ngày"
                                                name="toDate"
                                                placeholder="Đến từ"
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

export default memo(observer(VourcherFilter));