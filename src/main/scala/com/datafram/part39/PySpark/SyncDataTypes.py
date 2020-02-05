

def compareAndSyndDataTypes(self,df_source,df_target,matchHive=false):
    source_fields = set((col.name,col.datatype) for col in source.schema)
    target_fields = set((col.name,col.datatype) for col in targt.schema)

    source_types = (col.name: col.datatype for col in df_source.schema)
    main_types = (col.name: col.datatype for col in df_target.schema)

    flag=0
    for col_name,col_type in source_fields.difference(target_fields):
        if col_name in main_types and main_types[col_name] != source_types[col_name]:
            flag = 1
            if main_types[col_name] == 'StringType':
                existing_type=main_types[col_name]
            else:
                existing_type='String'
            df_source_new=df_source.withColumn(col_name,df_source[col_name].cast(existing_type))
            df_source=df_source_new
        else:
             print ("blah")
    if flag:
        df=df_source_new
    else:
        df=df_new
    return df

