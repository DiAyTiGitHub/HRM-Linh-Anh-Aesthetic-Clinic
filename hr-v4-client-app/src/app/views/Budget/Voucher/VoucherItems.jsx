import { Button, ButtonGroup, Grid, makeStyles } from "@material-ui/core";
import { FieldArray, useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import VoucherRow from "./VoucherRow";

const useStyles = makeStyles(() => ({
  root: {
    background: "#E4f5fc",
    padding: "10px 15px",
    borderRadius: "5px",
  },
  groupContainer: {
    width: "100%",
    "& .MuiOutlinedInput-root": {
      borderRadius: "0!important",
    },
  },
  tableContainer: {
    marginTop: "2px",
    overflowX: "auto",
    overflowY: "hidden",
    "& table": {
      border: "1px solid #ccc",
      borderCollapse: "collapse",
      "& td": {
        border: "1px solid #ccc",
      },
    },
  },
  tableHeader: {
    width: "100%",
    borderBottom: "1px solid #ccc",
    marginBottom: "8px",
    "& th": {
      width: "calc(100vw / 4)",
      border: "1px solid #ccc",
      padding: "8px 0 8px 4px",
    },
  },
}));

const VoucherItems = () => {
  const { values, setFieldValue } = useFormikContext();

  const classes = useStyles();
  const { t } = useTranslation();
  return (
    <FieldArray name='voucherItems'>
      {({ insert, remove, push }) => (
        <>
          <Grid item xs={12}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={4} md={3} lg={2}>
                <ButtonGroup color='container' aria-label='outlined primary button group'>
                  <Button
                    startIcon={<AddIcon />}
                    type='button'
                    onClick={() => {
                      push({
                        voucherItems: null,
                      });
                    }}>
                    Thêm khoản
                  </Button>
                </ButtonGroup>
              </Grid>
            </Grid>
          </Grid>

          <Grid item xs={12} style={{ overflowX: "auto" }}>
            <section className={classes.tableContainer}>
              <table className={classes.table}>
                <thead>
                  <tr className={classes.tableHeader}>
                    <th width='10%'>Thao tác</th>
                    <th width='20%'>{t("voucherItem.amount")} </th>
                    <th width='30%'>{t("voucherItem.name")}</th>
                    <th width='30%'>{t("voucherItem.note")}</th>
                  </tr>
                </thead>
                <tbody>
                  {values?.voucherItems?.length > 0 ? (
                    values?.voucherItems?.map((order, index) => (
                      <VoucherRow
                        key={index}
                        index={index}
                        voucherItems={values?.voucherItems}
                        nameSpace={`voucherItems[${index}]`}
                        remove={() => remove(index)}
                        push={() => push(index)}
                        //  disabled={!hasEditPermission}
                      />
                    ))
                  ) : (
                    <tr className='row-table-body row-table-no_data'>
                      <td colSpan={5} align='center' className='py-8'>
                        Chưa có thông tin
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </section>
          </Grid>
        </>
      )}
    </FieldArray>
  );
};

export default VoucherItems;
