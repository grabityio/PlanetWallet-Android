package io.grabity.planetwallet.Widgets.AdavanceRecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class AdvanceRecyclerView extends RecyclerView {

    public static final int VERTICAL = 0x0000;
    public static final int HORIZONTAL = 0x0001;
    public static final int GRID = 0x0100;
    public static final int STAGGERED = 0x1000;

    public ArrayList< Integer > headerViews;
    public ArrayList< Integer > footerViews;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnDetectEndScrollListener onDetectEndScrollListener;
    private OnEndItemListener onEndItemListener;
    private AdvanceArrayAdapter.OnAttachViewListener onAttachViewListener;

    public AdvanceRecyclerView( Context context ) {
        super( context );
        viewInit( );
    }

    public AdvanceRecyclerView( Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
        viewInit( );
    }

    public AdvanceRecyclerView( Context context, @Nullable AttributeSet attrs, int defStyle ) {
        super( context, attrs, defStyle );
        viewInit( );
    }

    private void viewInit( ) {
        setLayoutManager( new LinearLayoutManager( getContext( ), LinearLayoutManager.VERTICAL, false ) );
    }

    public void setLayout( int layout ) {
        setLayout( layout, 3 );
    }


    public void setLayout( int layout, int spanCount ) {
        switch ( layout ) {
            case VERTICAL:
                setLayoutManager( new LinearLayoutManager( getContext( ), LinearLayoutManager.VERTICAL, false ) );
                break;
            case HORIZONTAL:
                setLayoutManager( new LinearLayoutManager( getContext( ), LinearLayoutManager.HORIZONTAL, false ) );
                break;
            case GRID:
                setLayoutManager( new GridLayoutManager( getContext( ), spanCount, GridLayoutManager.VERTICAL, false ) );
                break;
            case GRID | HORIZONTAL:
                setLayoutManager( new GridLayoutManager( getContext( ), spanCount, GridLayoutManager.HORIZONTAL, false ) );
                break;
            case STAGGERED:
                setLayoutManager( new StaggeredGridLayoutManager( spanCount, LinearLayoutManager.VERTICAL ) );
                break;
            case STAGGERED | HORIZONTAL:
                setLayoutManager( new StaggeredGridLayoutManager( spanCount, LinearLayoutManager.HORIZONTAL ) );
                break;
        }

        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).setLayoutManager( getLayoutManager( ) );
        }
    }

    @Override
    public void setAdapter( Adapter adapter ) {
        if ( adapter instanceof AdvanceArrayAdapter ) {
            if ( headerViews != null && headerViews.size( ) > 0 ) {
                for ( Integer i : headerViews ) {
                    if ( i != null )
                        ( ( AdvanceArrayAdapter ) adapter ).addHeaderView( i );
                }
            }
            if ( footerViews != null && footerViews.size( ) > 0 ) {

                for ( Integer i : footerViews ) {
                    if ( i != null )
                        ( ( AdvanceArrayAdapter ) adapter ).addFooterView( i );
                }
            }
            if ( this.onAttachViewListener != null ) {
                ( ( AdvanceArrayAdapter ) adapter ).setOnAttachViewListener( onAttachViewListener );
            }

            ( ( AdvanceArrayAdapter ) adapter ).setLayoutManager( getLayoutManager( ) );

            ( ( AdvanceArrayAdapter ) adapter ).setOnItemClickListener( onItemClickListener );

            ( ( AdvanceArrayAdapter ) adapter ).setOnItemLongClickListener( onItemLongClickListener );

        }

        super.setAdapter( adapter );
    }

    public void addHeaderView( int resId ) {
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).addHeaderView( resId );
        } else {
            if ( headerViews == null ) headerViews = new ArrayList<>( );
            headerViews.add( resId );
        }
    }


    public void removeHeaderView( int resId ) {
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).removeHeaderView( resId );
        }
        if ( headerViews != null ) {
            headerViews.remove( resId );
        }
    }


    public void addFooterView( int resId ) {
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).addFooterView( resId );
        } else {
            if ( footerViews == null ) footerViews = new ArrayList<>( );
            footerViews.add( resId );
        }
    }

    public OnItemClickListener getOnItemClickListener( ) {
        return onItemClickListener;
    }

    public void setOnItemClickListener( OnItemClickListener onItemClickListener ) {
        this.onItemClickListener = onItemClickListener;
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).setOnItemClickListener( onItemClickListener );
        }
    }

    public OnItemLongClickListener getOnItemLongClickListener( ) {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener( OnItemLongClickListener onItemLongClickListener ) {
        this.onItemLongClickListener = onItemLongClickListener;
        if ( getAdapter( ) != null && getAdapter( ) instanceof AdvanceArrayAdapter ) {
            ( ( AdvanceArrayAdapter ) getAdapter( ) ).setOnItemLongClickListener( onItemLongClickListener );
        }
    }

    public interface OnItemClickListener {
        void onItemClick( AdvanceRecyclerView recyclerView, View view, int position );
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick( AdvanceRecyclerView recyclerView, View view, int position );
    }

    public interface OnEndItemListener {
        void onLoadMore( int page, int totalItemsCount, RecyclerView view );
    }

    public OnEndItemListener getOnEndItemListener( ) {
        return onEndItemListener;
    }

    public void resetState( ) {
        if ( onDetectEndScrollListener != null ) {
            onDetectEndScrollListener.resetState( );
        }
    }

    public void setOnEndItemListener( OnEndItemListener onEndItemListener ) {
        this.onEndItemListener = onEndItemListener;
        if ( onDetectEndScrollListener != null ) {
            this.removeOnScrollListener( onDetectEndScrollListener );
        }

        if ( getLayoutManager( ) instanceof LinearLayoutManager ) {
            onDetectEndScrollListener = new OnDetectEndScrollListener( ( LinearLayoutManager ) getLayoutManager( ) );
        } else if ( getLayoutManager( ) instanceof GridLayoutManager ) {
            onDetectEndScrollListener = new OnDetectEndScrollListener( ( GridLayoutManager ) getLayoutManager( ) );
        } else if ( getLayoutManager( ) instanceof StaggeredGridLayoutManager ) {
            onDetectEndScrollListener = new OnDetectEndScrollListener( ( StaggeredGridLayoutManager ) getLayoutManager( ) );
        }

        addOnScrollListener( onDetectEndScrollListener );
    }

    private class OnDetectEndScrollListener extends EndlessRecyclerViewScrollListener {

        public OnDetectEndScrollListener( LinearLayoutManager layoutManager ) {
            super( layoutManager );
        }

        public OnDetectEndScrollListener( GridLayoutManager layoutManager ) {
            super( layoutManager );
        }

        public OnDetectEndScrollListener( StaggeredGridLayoutManager layoutManager ) {
            super( layoutManager );
        }

        @Override
        public void onLoadMore( int page, int totalItemsCount, RecyclerView view ) {
            if ( onEndItemListener != null )
                onEndItemListener.onLoadMore( page, totalItemsCount, view );
        }
    }


    private class OnDefScrollListener extends RecyclerView.OnScrollListener {

        private float scrollX = 0;
        private float scrollY = 0;

        @Override
        public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
            super.onScrolled( recyclerView, dx, dy );
            scrollX += dx;
            scrollY += dy;
            if ( onScrollListener != null ) {
                onScrollListener.onScrolled( recyclerView, dx, dy, scrollX, scrollY );
            }
        }
    }

    private OnDefScrollListener defScrollListener;

    private OnScrollListener onScrollListener;

    public OnScrollListener getOnScrollListener( ) {
        return onScrollListener;
    }

    public void setOnScrollListener( OnScrollListener onScrollListener ) {
        this.onScrollListener = onScrollListener;
        defScrollListener = new OnDefScrollListener( );
        addOnScrollListener( defScrollListener );
    }

    public interface OnScrollListener {
        void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY );
    }


    public Bitmap getScreenshot( ) {
        AdvanceArrayAdapter adapter = ( AdvanceArrayAdapter ) this.getAdapter( );
        if ( adapter == null ) return null;
        int size = adapter.getItemCount( );
        int height = 0;
        Paint paint = new Paint( );
        int iHeight = 0;
        final int maxMemory = ( int ) ( Runtime.getRuntime( ).maxMemory( ) / 1024 );

        final int cacheSize = maxMemory / 8;
        LruCache< String, Bitmap > bitmaCache = new LruCache<>( cacheSize );

        for ( int i = 0; i < size; i++ ) {
            ViewHolder holder = adapter.createViewHolder( this, adapter.getItemViewType( i ) );
            adapter.onBindViewHolder( ( AdvanceArrayAdapter.ViewMapper ) holder, i );
            holder.itemView.measure( MeasureSpec.makeMeasureSpec( this.getWidth( ), MeasureSpec.EXACTLY ),
                    MeasureSpec.makeMeasureSpec( 0, MeasureSpec.UNSPECIFIED ) );
            holder.itemView.layout( 0, 0, holder.itemView.getMeasuredWidth( ), holder.itemView.getMeasuredHeight( ) );
            holder.itemView.setDrawingCacheEnabled( true );
            holder.itemView.buildDrawingCache( );
            Bitmap drawingCache = holder.itemView.getDrawingCache( );
            if ( drawingCache != null ) {
                bitmaCache.put( String.valueOf( i ), drawingCache );
            }
            height += holder.itemView.getMeasuredHeight( );
        }

        Bitmap bigBitmap = Bitmap.createBitmap( this.getMeasuredWidth( ), height, Bitmap.Config.ARGB_8888 );
        Canvas bigCanvas = new Canvas( bigBitmap );
        bigCanvas.drawColor( Color.WHITE );

        for ( int i = 0; i < size; i++ ) {
            Bitmap bitmap = bitmaCache.get( String.valueOf( i ) );
            if ( bitmap != null ) {
                bigCanvas.drawBitmap( bitmap, 0f, iHeight, paint );
                iHeight += bitmap.getHeight( );
                bitmap.recycle( );
            }
        }

        return bigBitmap;
    }

    public void setOnAttachViewListener( AdvanceArrayAdapter.OnAttachViewListener onAttachViewListener ) {
        this.onAttachViewListener = onAttachViewListener;
    }

}
