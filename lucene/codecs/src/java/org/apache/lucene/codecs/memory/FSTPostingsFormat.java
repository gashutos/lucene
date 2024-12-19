/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.codecs.memory;

import java.io.IOException;

import org.apache.lucene.backward_codecs.lucene90.Lucene90PostingsReader;
import org.apache.lucene.backward_codecs.lucene90.Lucene90PostingsWriter;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.codecs.lucene99.Lucene99PostingsReader;
import org.apache.lucene.codecs.lucene99.Lucene99PostingsWriter;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.IOUtils;

import static org.apache.lucene.codecs.Codec.LuceneCodec;

/** FST term dict + Lucene50PBF */
public final class FSTPostingsFormat extends PostingsFormat {
  public FSTPostingsFormat() {
    super("FST50");
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public FieldsConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
    PostingsWriterBase postingsWriter = (LuceneCodec == "Lucene95") ? new Lucene90PostingsWriter(state) : new Lucene99PostingsWriter(state);

    boolean success = false;
    try {
      FieldsConsumer ret = new FSTTermsWriter(state, postingsWriter);
      success = true;
      return ret;
    } finally {
      if (!success) {
        IOUtils.closeWhileHandlingException(postingsWriter);
      }
    }
  }

  @Override
  public FieldsProducer fieldsProducer(SegmentReadState state) throws IOException {
    PostingsReaderBase postingsReader = (LuceneCodec == "Lucene95") ? new Lucene90PostingsReader(state) : new Lucene99PostingsReader(state);
    boolean success = false;
    try {
      FieldsProducer ret = new FSTTermsReader(state, postingsReader);
      success = true;
      return ret;
    } finally {
      if (!success) {
        IOUtils.closeWhileHandlingException(postingsReader);
      }
    }
  }
}
