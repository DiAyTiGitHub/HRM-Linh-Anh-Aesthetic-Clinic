import { Button , ButtonGroup , Collapse , Grid } from '@material-ui/core';
import HighlightOffIcon from '@material-ui/icons/HighlightOff';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import GlobitsPagingAutocompleteV2 from 'app/common/form/GlobitsPagingAutocompleteV2';
import { useStore } from 'app/stores';
import { useFormikContext } from "formik";
import { observer } from 'mobx-react';
import { memo , useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import LocalConstants from "../../LocalConstants";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import { pagingAdministratives } from "./AdministrativeUnitService";

function AdministrativeFilter(props) {
    const {administrativeUnitStore} = useStore();
    const {t} = useTranslation();
    const {values , setFieldValue} = useFormikContext();
    const {
        intactSearchObject ,
        search ,
        handleSetSearchObject ,
        searchObject
    } = administrativeUnitStore;

    const {
        isOpenFilter ,
        handleFilter ,
        handleCloseFilter
    } = props;

    function handleResetFilter() {
        const newSearchObject = {
            ... JSON.parse(JSON.stringify(intactSearchObject)) ,
        };
        handleFilter(newSearchObject);
    }

    const handleChangeProvince = () => {
        setFieldValue("currentDistrict" , null);
        setFieldValue("currentWard" , null);
    };

    const handleChangeDictrict = () => {
        setFieldValue("currentWard" , null);
    };
    useEffect(() => {
        const newSearchObject = {
            ... values , pageIndex:1 , pageSize:searchObject.pageSize ,
        }
        handleSetSearchObject(newSearchObject);
        search();
    } , [values?.currentProvince?.id , values?.currentDistrict?.id])

    return (
        <Collapse in={isOpenFilter} className="filterPopup">
            <div className="flex flex-column">
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <div className="filterContent pt-8">
                            <Grid container spacing={2} className={"flex flex-end"}>
                                <Grid item xs={12} md={4}>
                                    <GlobitsSelectInput
                                        label={t("administrativeUnit.levelUnit")}
                                        name="level"
                                        keyValue="value"
                                        options={LocalConstants.AdminitractiveLevel}
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsPagingAutocompleteV2
                                        label={t("administrativeUnit.province")}
                                        name="currentProvince"
                                        api={pagingAdministratives}
                                        searchObject={{level:3}}
                                        handleChange={(_ , value) => {
                                            setFieldValue("currentProvince" , value)
                                            handleChangeProvince();
                                        }}
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsPagingAutocompleteV2
                                        label={t("humanResourcesInformation.district")}
                                        name="currentDistrict"
                                        api={pagingAdministratives}
                                        searchObject={{
                                            level:2 ,
                                            provinceId:values?.currentProvince?.id ,
                                        }}
                                        allowLoadOptions={!!values?.currentProvince?.id}
                                        clearOptionOnClose
                                        handleChange={(_ , value) => {
                                            setFieldValue("currentDistrict" , value)
                                            handleChangeDictrict();
                                        }}
                                    />
                                </Grid>
                                <Grid item xs={12} md={4}>
                                    <GlobitsPagingAutocompleteV2
                                        label={t("humanResourcesInformation.wards")}
                                        name="currentWard"
                                        api={pagingAdministratives}
                                        searchObject={{
                                            level:1 ,
                                            provinceId:values?.currentProvince?.id ,
                                            districtId:values?.currentDistrict?.id ,
                                        }}
                                        allowLoadOptions={!!values?.currentDistrict?.id}
                                        clearOptionOnClose
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

export default memo(observer(AdministrativeFilter));