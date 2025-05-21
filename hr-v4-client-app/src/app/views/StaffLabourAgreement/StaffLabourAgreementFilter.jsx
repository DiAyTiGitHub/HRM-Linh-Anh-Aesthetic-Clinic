import { Button, ButtonGroup, Collapse, Grid } from "@material-ui/core";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { observer } from "mobx-react";
import React, { memo } from "react";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import { pagingAllOrg } from "../Organization/OrganizationService";
import LocalConstants from "../../LocalConstants";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import SearchIcon from '@material-ui/icons/Search';

function StaffLabourAgreementFilter(props) {
    const { staffLabourAgreementStore } = useStore();
    const { t } = useTranslation();

    const { searchObject, intactSearchObject } = staffLabourAgreementStore;

    const { isOpenFilter, handleFilter, handleCloseFilter } = props;

    function handleResetFilter() {
        const newSearchObject = {
            ...JSON.parse(JSON.stringify(intactSearchObject)),
            pageIndex: searchObject.pageIndex,
            pageSize: searchObject.pageSize,
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
                                <Grid item xs={12} className='pb-0'>
                                    <p className='m-0 p-0 borderThrough2'>Tiêu chí</p>
                                </Grid>

                                <Grid item xs={12} sm={6} md={4} lg={3}>
                                    <GlobitsPagingAutocompleteV2
                                        name='staff'
                                        label={"Nhân viên áp dụng"}
                                        api={pagingStaff}
                                        getOptionLabel={(option) =>
                                            `${option?.displayName}` + " - " + `${option?.staffCode}`
                                        }
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6} md={4} lg={3}>
                                    <GlobitsSelectInput
                                        label={"Trạng thái hợp đồng"}
                                        name='agreementStatus'
                                        keyValue='value'
                                        options={LocalConstants.AgreementStatus.getListData()}
                                    />
                                </Grid>
                                <Grid item xs={12} sm={6} md={4} lg={3}>
                                    <GlobitsDateTimePicker
                                        label='Từ ngày'
                                        // disableFuture
                                        name='fromDate'
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} md={4} lg={3}>
                                    <GlobitsDateTimePicker
                                        label='Đến ngày'
                                        // disableFuture
                                        name='toDate'
                                    />
                                </Grid>

                                <Grid item xs={12} className='pb-0'>
                                    <p className='m-0 p-0 borderThrough2'>Lọc theo đơn vị</p>
                                </Grid>

                                <Grid item xs={12}>
                                    <Grid container spacing={2} className='flex flex-end'>
                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name='contractOrganization'
                                                label='Đơn vị kí hợp đồng'
                                                api={pagingAllOrg}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4} lg={3}>
                                            <GlobitsPagingAutocompleteV2
                                                name='workOrganization'
                                                label='Đơn vị làm việc'
                                                api={pagingAllOrg}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>
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
                                        </Button>{" "}
                                        <Button
                                            startIcon={<SearchIcon className={``} />}
                                            className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                            type='submit'>
                                            Tìm kiếm
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

export default memo(observer(StaffLabourAgreementFilter));
