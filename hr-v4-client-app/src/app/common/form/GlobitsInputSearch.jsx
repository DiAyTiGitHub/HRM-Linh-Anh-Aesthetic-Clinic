import React, { memo } from 'react'
import { FastField, Field, getIn } from 'formik';
import { FormControl } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import "../SearchBox.scss";

const GlobitsInputSearch = (props) => (
    <FastField  {...props} name={props.name} shouldUpdate={shouldComponentUpdate}>
        {({ field }) => <Component {...props} field={field} />}
    </FastField>
);

function Component({ name, hideSubmitIcon, field }) {
    return (
        <FormControl fullWidth>
            <div className="seaarch-box">
                <Field name={name} id={name} value={field?.value || ""} />
                {!hideSubmitIcon && (
                    <button type='submit' className='btn-search flex items-center justify-center'>
                        <SearchIcon />
                    </button>
                )}
            </div>
        </FormControl>
    )
}

const shouldComponentUpdate = (nextProps, currentProps) => {
    return (
        nextProps?.value !== currentProps?.value ||
        nextProps?.name !== currentProps?.name ||
        Object.assign(nextProps).length !== Object.assign(currentProps).length ||
        getIn(nextProps.formik.values, currentProps.name) !== getIn(currentProps.formik.values, currentProps.name) ||
        getIn(nextProps.formik.errors, currentProps.name) !== getIn(currentProps.formik.errors, currentProps.name) ||
        getIn(nextProps.formik.touched, currentProps.name) !== getIn(currentProps.formik.touched, currentProps.name)
    );
};


export default memo(GlobitsInputSearch);