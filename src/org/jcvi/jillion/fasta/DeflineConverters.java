package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Common implementations of {@link DeflineConverter}s.
 *
 * @since 6.1
 */
public final class DeflineConverters {

    /**
     * Just Return the same Defline that was passed in.
     */
    public static DeflineConverter identity(){
        return Defline::of;
    }

    /**
     * Concatenate the comment (if it exists)
     * to the id separated by a space.
     * @return a new {@link DeflineConverter}.
     */
    public static DeflineConverter concatenateComment(){
        return (id,comment)->{
            if(comment==null){
                return Defline.of(id);
            }
            return Defline.of(id +" " + comment);
        };
    }
    /**
     * Replace all spaces on the Defline with the given separator
     * to the id separated by a space.
     *
     * @param separator the new separator to use; can not be null.
     * @return a new {@link DeflineConverter}.
     * @throws NullPointerException if separator is null.
     */
    public static DeflineConverter convertAllSpacesTo(String separator){
        Objects.requireNonNull(separator);
        return (id,comment)->{
            String concatenated =comment==null? id :  id +" " + comment;
            return Defline.of(concatenated.replace(" ", separator));
        };
    }

    /**
     * Change the Id part of a defline into something else.
     * @param idMapper a Function; if the function is null, then no mapping is performed;
     *                if the function returns null, then the returned Defline will be null.
     * @return a new {@link DeflineConverter}.
     *
     * @implNote this is the same as
     * <pre>
     * {@code
     *
     *  if(idMapper==null){
     *    //nothing to map
     *    return Defline::of;
     *  }
     *  return map(idMapper, null);
     *
     * }
     * </pre>
     * 
     * @see #map(Function, Function)
     */
    public static DeflineConverter map(Function<String,String> idMapper){
        if(idMapper==null){
            //nothing to map
            return Defline::of;
        }
        return map(idMapper, null);
    }

    /**
     * Change the Id part of a defline into something else.
     * @param idAndCommentMapper a Function; if the function is null, then no mapping is performed;
     *                if the function returns null, then the returned Defline will be null.
     * @return a new {@link DeflineConverter}.
     *
     * @implNote this is the same as
     * <pre>
     * {@code
     *
     *  if(idAndCommentMapper==null){
     *    //nothing to map
     *    return Defline::of;
     *  }
     *  return idAndCommentMapper::apply;
     *
     * }
     * </pre>
     *
     * @see #map(Function, Function)
     */
    public static DeflineConverter map(BiFunction<String,String, Defline> idAndCommentMapper){
        if(idAndCommentMapper==null){
            //nothing to map
            return Defline::of;
        }
        return idAndCommentMapper::apply;
    }

    /**
     * Change the Id and comment parts of a defline into something else.
     * @param idMapper a Function; if the function is null, then the id is unchanged;
     *                if the function returns null, then the returned Defline will be null.
     * @param commentMapper a Function; if the function is null, then the comment is unchanged;
     *                      NOTE: the comment passed into this function will often be null.
     *
     * @return a new {@link DeflineConverter}.
     */
    public static DeflineConverter map(Function<String,String> idMapper, Function<String,String> commentMapper){
        if(idMapper==null && commentMapper==null){
            //nothing to map
            return Defline::of;
        }
        return (id,comment)->{
            String mappedId = idMapper==null? id: idMapper.apply(id);
            if(mappedId ==null){
                //return null don't bother mapping comment
                return null;
            }
            String mappedComment = commentMapper==null? comment: commentMapper.apply(comment);

            return Defline.of(mappedId, mappedComment);
        };
    }

    /**
     * Change the Id and comment parts of a defline into something else.
     * @param idMapper a BiFunction taking both the input id and comment; if the function is null, then the id is unchanged;
     *                if the function returns null, then the returned Defline will be null.
     *                 NOTE: the comment passed into this function will often be null.
     * @param commentMapper a BiFunction taking both the input id and comment; if the function is null, then the comment is unchanged;
     *                      NOTE: the comment passed into this function will often be null.
     *      *
     * @return a new {@link DeflineConverter}.
     */
    public static DeflineConverter map(BiFunction<String,String,String> idMapper, BiFunction<String,String,String> commentMapper){
        if(idMapper==null && commentMapper==null){
            //nothing to map
            return Defline::of;
        }
        return (id,comment)->{
            String mappedId = idMapper==null? id: idMapper.apply(id, comment);
            if(mappedId ==null){
                //return null don't bother mapping comment
                return null;
            }
            String mappedComment = commentMapper==null? comment: commentMapper.apply(id, comment);

            return Defline.of(mappedId, mappedComment);
        };
    }


}
