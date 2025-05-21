import React, { useState, useEffect, memo, useMemo } from "react";
import { Grid, DialogActions, Button, DialogContent, makeStyles, Tooltip } from "@material-ui/core";
import Skeleton from '@material-ui/lab/Skeleton';

const useStyles = makeStyles(() => ({
  tableContainer: {
    marginTop: "2px",
    overflowX: "auto",
    "& table": {
      border: "1px solid #ccc",
      borderCollapse: "collapse",
      "& td": {
        border: "1px solid #ccc",
      },
      "& th": {
        border: "1px solid #ccc",
      },
    },
  },
  tableHeader: {
    "& th": {
      padding: "8px 0 8px 4px",
    },
  },
}));

const PreviewSalaryResultLazySkeleton = () => {
  const classes = useStyles();

  return (
    <div className="dialog-body">
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <section className={classes.tableContainer}>
            <table className="w-100" style={{ tableLayout: "auto" }}>
              <thead>
                <tr className={classes.tableHeader}>
                  {/* Simulating grouped columns */}
                  {[...Array(4)].map((_, index) => (
                    <th key={index} colSpan={2}>
                      <Skeleton variant="text" width="100%" height={30} />
                    </th>
                  ))}
                </tr>
                <tr className={classes.tableHeader}>
                  {/* Simulating individual column headers */}
                  {[...Array(8)].map((_, index) => (
                    <th key={index}>
                      <Skeleton variant="text" width="100%" height={30} />
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {/* Simulating table rows with placeholders */}
                {[...Array(5)].map((_, rowIndex) => (
                  <tr key={rowIndex}>
                    {[...Array(8)].map((_, cellIndex) => (
                      <td key={cellIndex} style={{ padding: "6px" }}>
                        <Skeleton variant="rect" height={30} />
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </section>
        </Grid>
      </Grid>
    </div>
  );
};

export default memo(PreviewSalaryResultLazySkeleton);
