import { Grid, IconButton } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import GlobitsPagingAutocompleteV2 from "../../../../common/form/GlobitsPagingAutocompleteV2";
import { OrganizationType } from "../../../../LocalConstants";

function DepartmentToolbar ({setselectedOrg, handleFilter}) {
  const {values, setFieldValue} = useFormikContext ();

  useEffect (() => {
    pagingAllOrg ({pageIndex:1, pageSize:1, organizationType:OrganizationType.OPERATION.value})
        .then (({data}) => {
          if (data?.content && data?.content?.length > 0) {
            const department = data?.content[0];
            setselectedOrg (department);
            setFieldValue ("organization", department);
            handleFilter (department);
          }
        })
        .catch ((err) => {
          console.error (err);
        });
  }, []);

  return (
      <Grid container spacing={2} className={"flex justify-end align-center"}>
        <Grid item xs={12}>
          <GlobitsPagingAutocompleteV2
              name='organization'
              api={pagingAllOrg}
              style={{width:"100%"}}
              handleChange={(_, value) => {
                setFieldValue ("organization", value);
                setselectedOrg (value);
                handleFilter (value);
              }}
              searchObject={{
                organizationType:OrganizationType.OPERATION.value,
              }}
              endAdornment={
                <IconButton style={{marginTop:"-5px"}} type='submit'>
                  <SearchIcon className='text-primary'/>
                </IconButton>
              }
          />
        </Grid>
      </Grid>
  );
}

export default memo (observer (DepartmentToolbar));
