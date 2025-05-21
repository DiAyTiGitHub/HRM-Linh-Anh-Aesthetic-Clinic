import { Button , Grid , Tooltip } from "@material-ui/core";
import TouchAppIcon from "@material-ui/icons/TouchApp";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo , useState } from "react";
import { useTranslation } from "react-i18next";
import ObjectSelectorPopup from "./ObjectSelectorPopup";

function ObjectSelectorSection(props) {
    const {t} = useTranslation();
    const {
        label ,
        placeholder = "Chưa chọn dữ liệu" ,
        required = false ,
        disabled = false ,
        disabledTextFieldOnly = false ,
        name = "selectedObject" ,
        handleAfterSubmit ,
        popupTitle ,
        columns = [] ,
        fetchDataFunction ,
        resetDataFunction ,
        fetchAutocompleteFunction ,
        searchObjectKey = "keyword" ,
        searchPlaceholder ,
        dataList = [] ,
        totalElements = 0 ,
        totalPages = 0 ,
        handleChangePage ,
        setPageSize ,
        searchObject = {} ,
        handleSetSearchObject ,
        getOptionLabel = (option) => option?.staff?.displayName || "" ,
        popupSize = "md" ,
        buttonTooltip = "Chọn" ,
        customFilter ,
        readOnly ,
    } = props;

    const [openPopup , setOpenPopup] = useState(false);

    function handleClosePopup() {
        setOpenPopup(false);
    }

    const {values , setFieldValue} = useFormikContext();

    return (
        <>
            {!disabled && (
                <Grid container spacing={1}>

                    <Grid item xs={9}>
                        <GlobitsPagingAutocomplete
                            name={name}
                            label={label || "Dữ liệu"}
                            api={fetchAutocompleteFunction}
                            required={required}
                            placeholder={placeholder}
                            disabled={true}
                            getOptionLabel={getOptionLabel}
                            readOnly={readOnly}
                        />
                    </Grid>

                    <Grid item xs={3} className='flex align-end'>
                        <Tooltip placement='top' title={buttonTooltip}>
                            <Button
                                fullWidth
                                variant='contained'
                                className='btn bgc-lighter-dark-blue text-white d-inline-flex my-2'
                                onClick={() => setOpenPopup(true)}
                                disabled={disabled || readOnly}>
                                <TouchAppIcon className='text-white'/>
                            </Button>
                        </Tooltip>
                    </Grid>
                </Grid>
            )}

            {disabled && (
                <GlobitsPagingAutocomplete
                    name={name}
                    label={label || "Dữ liệu"}
                    api={fetchAutocompleteFunction}
                    required={required}
                    placeholder={placeholder}
                    disabled={true}
                    getOptionLabel={getOptionLabel}
                    readOnly={readOnly}
                />
            )}

            {openPopup && (
                <ObjectSelectorPopup
                    open={openPopup}
                    handleClose={handleClosePopup}
                    disabled={disabled}
                    name={name}
                    handleAfterSubmit={handleAfterSubmit}
                    title={popupTitle}
                    columns={columns}
                    fetchDataFunction={fetchDataFunction}
                    resetDataFunction={resetDataFunction}
                    searchObjectKey={searchObjectKey}
                    searchPlaceholder={searchPlaceholder}
                    dataList={dataList}
                    totalElements={totalElements}
                    totalPages={totalPages}
                    handleChangePage={handleChangePage}
                    setPageSize={setPageSize}
                    searchObject={searchObject}
                    handleSetSearchObject={handleSetSearchObject}
                    size={popupSize}
                    customFilter={customFilter}
                />
            )}
        </>
    );
}

export default memo(observer(ObjectSelectorSection));
