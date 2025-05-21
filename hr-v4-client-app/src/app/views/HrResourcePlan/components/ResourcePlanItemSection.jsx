import { Grid, makeStyles } from "@material-ui/core";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { useStore } from "app/stores";
import { getResourceItemById } from "app/views/Position/PositionService";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

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
      border: "1px solid #ccc",
      padding: "8px 0 8px 4px",
    },
  },
}));

function ResourcePlanItemSection({ readOnly }) {
  const { t } = useTranslation();
  const classes = useStyles();
  const { values, setFieldValue } = useFormikContext();
  const { hrResourcePlanStore } = useStore();
  const { selectedHrResourcePlan } = hrResourcePlanStore;

  const [originalResourcePlanItems, setOriginalResourcePlanItems] = useState([]);

  useEffect(() => {
    const currentDepartmentId = values?.department?.id;
    const originalDepartmentId = selectedHrResourcePlan?.department?.id;
    const isNew = !selectedHrResourcePlan?.id;

    if (currentDepartmentId == null) return;

    const isChanged = currentDepartmentId !== originalDepartmentId;

    if (isNew || isChanged) {
      getResourceItemById(currentDepartmentId)
        .then(({ data }) => {
          const updatedData = data.map((item) => {
            const currentPositionNumber = item.currentPositionNumber || 0;
            const currentStaffNumber = item.currentStaffNumber || 0;

            return {
              ...item,
              additionalNumber: Math.max(currentPositionNumber - currentStaffNumber, 0),
              eliminatePlanNumber: Math.max(currentStaffNumber - currentPositionNumber, 0),
            };
          });

          // Nếu là lần đầu thì lưu giá trị gốc lại để sau này khôi phục
          if (!originalResourcePlanItems.length && !isNew) {
            setOriginalResourcePlanItems(values.resourcePlanItems || []);
          }

          setFieldValue("resourcePlanItems", updatedData);
        })
        .catch((err) => {
          console.log(err);
        });
    } else {
      // Quay lại phòng ban ban đầu => khôi phục giá trị ban đầu
      if (originalResourcePlanItems.length) {
        setFieldValue("resourcePlanItems", originalResourcePlanItems);
      }
    }
  }, [values?.department?.id, selectedHrResourcePlan?.id, setFieldValue]);


  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <FieldArray name='resourcePlanItems'>
          {({ push, remove }) => (
            <>

              <Grid item xs={12} className="pt-0">
                <section className={classes.tableContainer}>
                  <table className={`w-100 ${classes.table}`}>
                    <thead>
                      <tr className={classes.tableHeader}>
                        <th align='center' style={{ width: "10%" }}>
                          {t("Mã")}
                        </th>
                        <th align='center' style={{ width: "30%" }}>
                          {t("Chức danh")}
                        </th>
                        <th align='center' style={{ width: "10%" }}>
                          {t("Số lượng định biên")}
                        </th>
                        <th align='center' style={{ width: "10%" }}>
                          {t("Số lượng thực tế")}
                        </th>
                        <th align='center' style={{ width: "10%" }}>
                          {t("Cần bổ sung")}
                        </th>
                        <th align='center' style={{ width: "10%" }}>
                          {t("Cần sa thải")}
                        </th>
                      </tr>
                    </thead>

                    <tbody>
                      {values?.resourcePlanItems?.length > 0 ? (
                        values?.resourcePlanItems?.map((item, index) => (
                          <ResourcePlanItem
                            key={index}
                            index={index}
                            order={item}
                            nameSpace={`resourcePlanItems[${index}]`}
                            remove={() => remove(index)}
                            readOnly={readOnly}
                          />
                        ))
                      ) : (
                        <tr className='row-table-body row-table-no_data'>
                          <td colSpan={6} align='center' className='py-8'>
                            Phòng ban chưa có chức danh nào
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
      </Grid>
    </Grid>
  );
}

const ResourcePlanItem = memo(({ index, nameSpace, order, readOnly }) => {
  const withNameSpace = (field) => (field ? `${nameSpace}.${field}` : nameSpace);
  const { values, setFieldValue } = useFormikContext();

  useEffect(() => {
    const currentPositionNumber = order?.currentPositionNumber || 0;
    const currentStaffNumber = order?.currentStaffNumber || 0;

    const additionalNumber = Math.max(currentPositionNumber - currentStaffNumber, 0);
    // const eliminatePlanNumber = Math.max(currentStaffNumber - currentPositionNumber, 0);

    setFieldValue(withNameSpace("additionalNumber"), additionalNumber);
    // setFieldValue(withNameSpace("eliminatePlanNumber"), eliminatePlanNumber);
  }, [order?.currentPositionNumber, order?.currentStaffNumber]);

  console.log(values?.[nameSpace]);

  return (
    <tr className='row-table-body' key={index}>
      <td>
        <span className="px-6">
          {order?.positionTitle?.code}
        </span>
      </td>

      <td>
        <span className="px-6">
          {order?.positionTitle?.name}
        </span>
      </td>

      <td>
        <GlobitsNumberInput
          readOnly
          name={withNameSpace("currentPositionNumber")}
        />
      </td>
      <td>
        <GlobitsNumberInput
          readOnly
          name={withNameSpace("currentStaffNumber")}
        />
      </td>
      <td>
        <GlobitsNumberInput
          readOnly
          name={withNameSpace("additionalNumber")}

        />
      </td>
      <td>
        <GlobitsNumberInput
          name={withNameSpace("eliminatePlanNumber")}
          readOnly={readOnly}
        />
      </td>
    </tr>
  );
});

export default memo(observer(ResourcePlanItemSection));
